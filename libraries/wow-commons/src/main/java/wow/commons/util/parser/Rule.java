package wow.commons.util.parser;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public interface Rule {
	boolean matchAndTakeAction(String line);

	static Rule exact(String exactValue, Runnable action) {
		return line -> {
			if (line.equals(exactValue)) {
				action.run();
				return true;
			}
			return false;
		};
	}

	static Rule prefix(String prefix, Consumer<String> action) {
		return line -> {
			String withoutPrefix = ParserUtil.removePrefix(prefix, line);
			if (withoutPrefix != null) {
				action.accept(withoutPrefix);
				return true;
			}
			return false;
		};
	}

	static <T> Rule tryParse(Function<String, T> lineParser, Consumer<T> parsedValueConsumer) {
		return line -> {
			T parsedValue = lineParser.apply(line);
			if (parsedValue != null) {
				parsedValueConsumer.accept(parsedValue);
				return true;
			}
			return false;
		};
	}

	static Rule test(Predicate<String> predicate, Consumer<String> consumer) {
		return line -> {
			if (predicate.test(line)) {
				consumer.accept(line);
				return true;
			}
			return false;
		};
	}

	static <T> Rule testNotNull(Function<String, T> lineParser, Consumer<String> consumer) {
		return test(line -> lineParser.apply(line) != null, consumer);
	}

	static Rule matches(String pattern, Consumer<String> consumer) {
		return test(line -> line.matches("^" + pattern + "$"), consumer);
	}

	static Rule regex(String regex, Consumer<ParsedMultipleValues> matchedValueConsumer) {
		return line -> {
			ParsedMultipleValues matchedValues = ParserUtil.parseMultipleValues(regex, line);
			if (matchedValues.isEmpty()) {
				return false;
			}
			matchedValueConsumer.accept(matchedValues);
			return true;
		};
	}
}

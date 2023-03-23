package wow.commons.util.parser;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
public final class ParserUtil {
	public static ParsedMultipleValues parseMultipleValues(String regex, String line) {
		Pattern pattern = Pattern.compile("^" + regex + "$");
		Matcher matcher = pattern.matcher(line);
		if (!matcher.find()) {
			return ParsedMultipleValues.EMPTY;
		}
		assertHasGroups(matcher, regex);
		String[] result = getMatchedGroups(matcher);
		return new ParsedMultipleValues(result);
	}

	public static String[] getMatchedGroups(Matcher matcher) {
		String[] result = new String[matcher.groupCount()];
		for (int i = 1; i <= matcher.groupCount(); ++i) {
			String group = matcher.group(i);
			result[i - 1] = group;
		}
		return result;
	}

	private static void assertHasGroups(Matcher matcher, String regex) {
		if (matcher.groupCount() == 0) {
			throw new IllegalArgumentException(String.format("Pattern '%s' has no groups, use String.matches instead!", regex));
		}
	}

	public static String removePrefix(String prefix, String line) {
		if (line.startsWith(prefix)) {
			return line.substring(prefix.length());
		}
		return null;
	}

	public static String substituteParams(String expression, IntFunction<String> paramProvider) {
		if (expression == null || expression.isBlank()) {
			return expression;
		}

		Pattern pattern = Pattern.compile("\\$(\\d)");
		Matcher matcher = pattern.matcher(expression);
		StringBuilder sb = new StringBuilder();

		while (matcher.find()) {
			int paramNo = Integer.parseInt(matcher.group(1));
			matcher.appendReplacement(sb, paramProvider.apply(paramNo));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	public static <T> List<T> getValues(String line, Function<String, T> elementMapper) {
		return Stream.of(line.split(", "))
				.map(elementMapper)
				.toList();
	}

	private ParserUtil() {}
}

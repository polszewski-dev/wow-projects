package wow.commons.util;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
public class ParserUtil {
	public static Object[] parseMultipleValues(String regex, String line) {
		Pattern pattern = Pattern.compile("^" + regex + "$");
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			Object[] result = new Object[matcher.groupCount()];
			for (int i = 1; i <= matcher.groupCount(); ++i) {
				String group = matcher.group(i);
				if (group != null && group.matches("\\d+")) {
					result[i - 1] = Integer.valueOf(group);
				} else {
					result[i - 1] = group;
				}
			}
			return result;
		}
		return null;
	}

	public static int[] parseMultipleInts(String regex, String value) {
		Pattern pattern = Pattern.compile("^" + regex + "$");
		Matcher matcher = pattern.matcher(value);
		if (matcher.find()) {
			int[] result = new int[matcher.groupCount()];
			for (int i = 1; i <= matcher.groupCount(); ++i) {
				result[i - 1] = Integer.parseInt(matcher.group(i));
			}
			return result;
		}
		return null;
	}

	public static String substituteParams(String expression, Function<Integer, String> paramProvider) {
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
				.collect(Collectors.toList());
	}
}

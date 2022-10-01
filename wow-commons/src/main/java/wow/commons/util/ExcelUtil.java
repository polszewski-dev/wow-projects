package wow.commons.util;

import polszewski.excel.reader.ExcelReader;
import wow.commons.model.Duration;
import wow.commons.model.Percent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public final class ExcelUtil {
	public static Map<String, Integer> getHeader(ExcelReader excelReader) {
		Map<String, Integer> result = new LinkedHashMap<>();

		while (excelReader.nextCell()) {
			String value = excelReader.getCurrentCellStringValue();
			if (value != null) {
				result.put(value.trim(), excelReader.getCurrentColIdx());
			}
		}
		return result;
	}

	public static Map<String, Integer> get2RowHeader(ExcelReader excelReader) {
		Map<String, Integer> result = new LinkedHashMap<>();

		for (int i = 1; i <= 2; ++i) {
			while (excelReader.nextCell()) {
				String value = excelReader.getCurrentCellStringValue();
				if (value != null) {
					result.put(value.trim(), excelReader.getCurrentColIdx());
				}
			}
			if (i == 1) {
				if (!excelReader.nextRow()) {
					break;
				}
			}
		}
		return result;
	}

	public static String getString(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return excelReader.getCellStringValue(header.get(col));
	}

	public static int getInteger(String col, ExcelReader excelReader, Map<String, Integer> header) {
		String str = getString(col, excelReader, header);
		if (str == null || str.isEmpty()) {
			return 0;
		}
		return Integer.parseInt(str);
	}

	public static Integer getNullableInteger(String col, ExcelReader excelReader, Map<String, Integer> header) {
		String str = getString(col, excelReader, header);
		if (str == null || str.isEmpty()) {
			return null;
		}
		return Integer.parseInt(str);
	}

	public static double getDouble(String col, ExcelReader excelReader, Map<String, Integer> header) {
		String str = getString(col, excelReader, header);
		if (str == null || str.isEmpty()) {
			return 0;
		}
		return Double.parseDouble(str);
	}

	public static Double getNullableDouble(String col, ExcelReader excelReader, Map<String, Integer> header) {
		String str = getString(col, excelReader, header);
		if (str == null || str.isEmpty()) {
			return null;
		}
		return Double.parseDouble(str);
	}

	public static Percent getPercent(String col, ExcelReader excelReader, Map<String, Integer> header) {
		String str = getString(col, excelReader, header);
		if (str == null || str.isEmpty()) {
			return Percent.ZERO;
		}
		return Percent.of(Double.parseDouble(str));
	}

	public static Percent getNullablePercent(String col, ExcelReader excelReader, Map<String, Integer> header) {
		String str = getString(col, excelReader, header);
		if (str == null || str.isEmpty()) {
			return null;
		}
		return Percent.of(Double.parseDouble(str));
	}

	public static boolean getBoolean(String col, ExcelReader excelReader, Map<String, Integer> header) {
		String str = getString(col, excelReader, header);
		if (str == null || str.isEmpty()) {
			return false;
		}
		if ("1".equals(str)) {
			return true;
		}
		if ("0".equals(str)) {
			return false;
		}
		return Boolean.parseBoolean(str);
	}

	public static Duration getDuration(String col, ExcelReader excelReader, Map<String, Integer> header) {
		Double seconds = getNullableDouble(col, excelReader, header);
		if (seconds == null) {
			return null;
		}
		return Duration.seconds(seconds);
	}

	public static <T> T getEnum(String col, Function<String, T> producer, ExcelReader excelReader, Map<String, Integer> header) {
		String str = getString(col, excelReader, header);
		if (str == null || str.isEmpty()) {
			return null;
		}
		return producer.apply(str.toUpperCase());
	}

	public static <T> Set<T> getSet(String col, Function<String, T> producer, ExcelReader excelReader, Map<String, Integer> header) {
		String str = getString(col, excelReader, header);
		if (str == null || str.isEmpty()) {
			return null;
		}
		return Stream.of(str.split(",")).map(x -> producer.apply(x.trim())).collect(Collectors.toSet());
	}

	public static List<String> getStringList(String col, String separator, ExcelReader excelReader, Map<String, Integer> header) {
		String string = excelReader.getCellStringValue(header.get(col));
		if (string == null || string.isEmpty()) {
			return null;
		}
		return List.of(string.split(Pattern.quote(separator)));
	}

	public static <T> List<T> getList(String col, Function<String, T> producer, ExcelReader excelReader, Map<String, Integer> header) {
		String str = getString(col, excelReader, header);
		if (str == null || str.isEmpty()) {
			return null;
		}
		return Stream.of(str.split(",")).map(x -> producer.apply(x.trim())).collect(Collectors.toList());
	}

	private ExcelUtil() {}
}

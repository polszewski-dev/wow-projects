package wow.commons.util;

import polszewski.excel.reader.ExcelReader;
import wow.commons.model.Duration;
import wow.commons.model.Percent;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
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

	public static Optional<String> getOptionalString(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return Optional.ofNullable(excelReader.getCellStringValue(header.get(col)));
	}

	public static String getString(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalString(col, excelReader, header)
				.orElseThrow(() -> new IllegalArgumentException("Column '" + col + "' is empty"));
	}

	public static OptionalInt getOptionalInteger(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalString(col, excelReader, header)
				.map(s -> OptionalInt.of(Integer.parseInt(s)))
				.orElse(OptionalInt.empty());
	}

	public static int getInteger(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalInteger(col, excelReader, header)
				.orElseThrow(() -> new IllegalArgumentException("Column '" + col + "' is empty"));
	}

	public static OptionalDouble getOptionalDouble(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalString(col, excelReader, header)
				.map(s -> OptionalDouble.of(Double.parseDouble(s)))
				.orElse(OptionalDouble.empty());
	}

	public static double getDouble(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalDouble(col, excelReader, header)
				.orElseThrow(() -> new IllegalArgumentException("Column '" + col + "' is empty"));
	}

	public static Optional<Percent> getOptionalPercent(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalString(col, excelReader, header)
				.map(seconds -> Percent.of(Double.parseDouble(seconds)));
	}

	public static Percent getPercent(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalPercent(col, excelReader, header)
				.orElseThrow(() -> new IllegalArgumentException("Column '" + col + "' is empty"));
	}

	public static boolean getBoolean(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalString(col, excelReader, header)
				.map(str -> {
					if ("1".equals(str)) {
						return true;
					}
					if ("0".equals(str)) {
						return false;
					}
					return Boolean.parseBoolean(str);
				})
				.orElse(false);
	}

	public static Optional<Duration> getOptionalDuration(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalString(col, excelReader, header)
				.map(seconds -> Duration.seconds(Double.parseDouble(seconds)));
	}

	public static Duration getDuration(String col, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalDuration(col, excelReader, header)
				.orElseThrow(() -> new IllegalArgumentException("Column '" + col + "' is empty"));
	}

	public static <T> Optional<T> getOptionalEnum(String col, Function<String, T> producer, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalString(col, excelReader, header).map(producer);
	}

	public static <T> T getEnum(String col, Function<String, T> producer, ExcelReader excelReader, Map<String, Integer> header) {
		return getOptionalEnum(col, producer, excelReader, header)
				.orElseThrow(() -> new IllegalArgumentException("Column '" + col + "' is empty"));
	}

	public static <T> List<T> getList(String col, Function<String, T> producer, ExcelReader excelReader, Map<String, Integer> header) {
		return getValues(col, producer, excelReader, header, Collectors.toList());
	}

	public static <T> Set<T> getSet(String col, Function<String, T> producer, ExcelReader excelReader, Map<String, Integer> header) {
		return getValues(col, producer, excelReader, header, Collectors.toSet());
	}

	private static <T, C extends Collection<T>> C getValues(String col, Function<String, T> producer, ExcelReader excelReader, Map<String, Integer> header, Collector<T, ?, C> collector) {
		return getOptionalString(col, excelReader, header)
				.stream()
				.flatMap(str -> Stream.of(str.split(",")))
				.map(x -> producer.apply(x.trim()))
				.collect(collector);
	}

	private ExcelUtil() {}
}

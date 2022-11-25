package wow.commons.util;

import polszewski.excel.reader.ExcelReader;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public abstract class ExcelSheetParser {
	protected final String sheetName;

	protected ExcelReader excelReader;
	protected Map<String, Integer> header;

	protected ExcelSheetParser(String sheetName) {
		this.sheetName = sheetName;
	}

	public void init(ExcelReader excelReader, Map<String, Integer> header) {
		this.excelReader = excelReader;
		this.header = header;
	}

	public void readSheet() {
		ExcelColumn columnIndicatingOptionalRow = getColumnIndicatingOptionalRow();
		while (excelReader.nextRow()) {
			if (columnIndicatingOptionalRow == null || columnIndicatingOptionalRow.getString(null) != null) {
				readSingleRow();
			}
		}
	}

	protected abstract ExcelColumn getColumnIndicatingOptionalRow();

	protected abstract void readSingleRow();

	protected ExcelColumn column(String name, boolean optional) {
		return new ExcelColumn(name, optional);
	}

	protected ExcelColumn column(String name) {
		return column(name, false);
	}

	protected class ExcelColumn {
		private final String name;
		private final boolean optional;

		public ExcelColumn(String name, boolean optional) {
			this.name = name;
			this.optional = optional;
		}

		public String getName() {
			return name;
		}

		public boolean isOptional() {
			return optional;
		}

		public String getString(String defaultValue) {
			return getOptionalString().orElse(defaultValue);
		}

		public String getString() {
			return getOptionalString().orElseThrow(this::columnIsEmpty);
		}

		public int getInteger(int defaultValue) {
			return getOptionalInteger().orElse(defaultValue);
		}

		public int getInteger() {
			return getOptionalInteger().orElseThrow(this::columnIsEmpty);
		}

		public double getDouble(double defaultValue) {
			return getOptionalDouble().orElse(defaultValue);
		}

		public double getDouble() {
			return getOptionalDouble().orElseThrow(this::columnIsEmpty);
		}

		public boolean getBoolean() {
			return getOptionalString()
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

		public <T> T getEnum(Function<String, T> producer, T defaultValue) {
			return getOptionalEnum(producer).orElse(defaultValue);
		}

		public <T> T getEnum(Function<String, T> producer) {
			return getOptionalEnum(producer).orElseThrow(this::columnIsEmpty);
		}

		public <T> List<T> getList(Function<String, T> producer) {
			return getList(producer, ",");
		}

		public <T> List<T> getList(Function<String, T> producer, String separator) {
			return getValues(producer, separator, Collectors.toList());
		}

		public <T> Set<T> getSet(Function<String, T> producer) {
			return getSet(producer, ",");
		}

		public <T> Set<T> getSet(Function<String, T> producer, String separator) {
			return getValues(producer, separator, Collectors.toSet());
		}

		public ExcelColumn multi(int index) {
			return new ExcelColumn(name + index, optional);
		}

		protected Optional<String> getOptionalString() {
			Integer colNo = header.get(name);
			if (colNo != null) {
				return Optional.ofNullable(excelReader.getCellStringValue(colNo));
			}
			if (optional) {
				return Optional.empty();
			}
			throw new IllegalArgumentException("No column: " + name);
		}

		private OptionalInt getOptionalInteger() {
			return getOptionalString()
					.map(s -> OptionalInt.of(Integer.parseInt(s)))
					.orElse(OptionalInt.empty());
		}

		private OptionalDouble getOptionalDouble() {
			return getOptionalString()
					.map(s -> OptionalDouble.of(Double.parseDouble(s)))
					.orElse(OptionalDouble.empty());
		}

		private <T> Optional<T> getOptionalEnum(Function<String, T> producer) {
			return getOptionalString().map(producer);
		}

		private <T, C extends Collection<T>> C getValues(Function<String, T> producer, String separator, Collector<T, ?, C> collector) {
			return getOptionalString()
					.stream()
					.flatMap(str -> Stream.of(str.split(separator)))
					.map(x -> producer.apply(x.trim()))
					.collect(collector);
		}

		protected IllegalArgumentException columnIsEmpty() {
			return new IllegalArgumentException("Column '" + name + "' is empty");
		}
	}
}

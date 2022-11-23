package wow.commons.util;

import polszewski.excel.reader.ExcelReader;
import wow.commons.model.Duration;
import wow.commons.model.Percent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public abstract class ExcelSheetReader {
	protected final String sheetName;

	protected ExcelReader excelReader;
	protected Map<String, Integer> header;

	protected ExcelSheetReader(String sheetName) {
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

	protected ExcelColumn column(String name) {
		return new ExcelColumn(name);
	}

	protected class ExcelColumn {
		private final String name;

		public ExcelColumn(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getString(String defaultValue) {
			return ExcelUtil.getOptionalString(name, excelReader, header).orElse(defaultValue);
		}

		public String getString() {
			return ExcelUtil.getString(name, excelReader, header);
		}

		public int getInteger(int defaultValue) {
			return ExcelUtil.getOptionalInteger(name, excelReader, header).orElse(defaultValue);
		}

		public int getInteger() {
			return ExcelUtil.getInteger(name, excelReader, header);
		}

		public double getDouble(double defaultValue) {
			return ExcelUtil.getOptionalDouble(name, excelReader, header).orElse(defaultValue);
		}

		public double getDouble() {
			return ExcelUtil.getDouble(name, excelReader, header);
		}

		public Percent getPercent(Percent defaultValue) {
			return ExcelUtil.getOptionalPercent(name, excelReader, header).orElse(defaultValue);
		}

		public Percent getPercent() {
			return ExcelUtil.getPercent(name, excelReader, header);
		}

		public boolean getBoolean() {
			return ExcelUtil.getBoolean(name, excelReader, header);
		}

		public Duration getDuration(Duration defaultValue) {
			return ExcelUtil.getOptionalDuration(name, excelReader, header).orElse(defaultValue);
		}

		public Duration getDuration() {
			return ExcelUtil.getDuration(name, excelReader, header);
		}

		public <T> T getEnum(Function<String, T> producer, T defaultValue) {
			return ExcelUtil.getOptionalEnum(name, producer, excelReader, header).orElse(defaultValue);
		}

		public <T> T getEnum(Function<String, T> producer) {
			return ExcelUtil.getEnum(name, producer, excelReader, header);
		}

		public <T> List<T> getList(Function<String, T> producer) {
			return ExcelUtil.getList(name, producer, excelReader, header);
		}

		public <T> List<T> getList(Function<String, T> producer, String separator) {
			return ExcelUtil.getList(name, producer, separator, excelReader, header);
		}

		public <T> Set<T> getSet(Function<String, T> producer) {
			return ExcelUtil.getSet(name, producer, excelReader, header);
		}

		public <T> Set<T> getSet(Function<String, T> producer, String separator) {
			return ExcelUtil.getSet(name, producer, separator, excelReader, header);
		}

		public ExcelColumn multi(int index) {
			return new ExcelColumn(name + index);
		}
	}
}

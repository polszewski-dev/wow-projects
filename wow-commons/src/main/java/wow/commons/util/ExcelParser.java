package wow.commons.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import polszewski.excel.reader.ExcelReader;
import polszewski.excel.reader.PoiExcelReader;
import wow.commons.model.Duration;
import wow.commons.model.Percent;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static wow.commons.util.ExcelUtil.getHeader;

/**
 * User: POlszewski
 * Date: 2022-10-13
 */
public abstract class ExcelParser {
	protected class SheetReader {
		public final String sheetName;
		private final Runnable rowConsumer;

		private final ExcelColumn columnIndicatingOptionalRow;

		public SheetReader(String sheetName, Runnable rowConsumer, ExcelColumn columnIndicatingOptionalRow) {
			this.sheetName = sheetName;
			this.rowConsumer = rowConsumer;
			this.columnIndicatingOptionalRow = columnIndicatingOptionalRow;
		}

		public SheetReader(String sheetName, Runnable rowConsumer) {
			this(sheetName, rowConsumer, null);
		}

		public void readSheet() {
			while (excelReader.nextRow()) {
				if (columnIndicatingOptionalRow == null || columnIndicatingOptionalRow.getString(null) != null) {
					rowConsumer.run();
				}
			}
		}
	}

	protected ExcelReader excelReader;
	protected Map<String, Integer> header;

	public final void readFromXls() throws IOException, InvalidFormatException {
		try (ExcelReader excelReader = new PoiExcelReader(getExcelInputStream())) {
			this.excelReader = excelReader;
			while (excelReader.nextSheet()) {
				if (excelReader.nextRow()) {
					this.header = getHeader(excelReader);
					getSheetReader(excelReader).readSheet();
				}
			}
		} finally {
			this.excelReader = null;
			this.header = null;
		}
	}

	private SheetReader getSheetReader(ExcelReader excelReader) {
		return getSheetReaders()
				.filter(reader -> reader.sheetName.equals(excelReader.getCurrentSheetName()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown sheet: " + excelReader.getCurrentSheetName()));
	}

	protected abstract InputStream getExcelInputStream();


	protected abstract Stream<SheetReader> getSheetReaders();

	protected InputStream fromResourcePath(String path) {
		return this.getClass().getResourceAsStream(path);
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

	protected ExcelColumn column(String name) {
		return new ExcelColumn(name);
	}
}

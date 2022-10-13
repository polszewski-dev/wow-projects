package wow.commons.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import polszewski.excel.reader.ExcelReader;
import polszewski.excel.reader.PoiExcelReader;
import wow.commons.model.Duration;
import wow.commons.model.Percent;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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

		private final String columnIndicatingOptionalRow;

		public SheetReader(String sheetName, Runnable rowConsumer, String columnIndicatingOptionalRow) {
			this.sheetName = sheetName;
			this.rowConsumer = rowConsumer;
			this.columnIndicatingOptionalRow = columnIndicatingOptionalRow;
		}

		public SheetReader(String sheetName, Runnable rowConsumer) {
			this(sheetName, rowConsumer, null);
		}

		public void readSheet() {
			while (excelReader.nextRow()) {
				if (columnIndicatingOptionalRow == null || getOptionalString(columnIndicatingOptionalRow).isPresent()) {
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
				if (!excelReader.nextRow()) {
					continue;
				}

				String currentSheetName = excelReader.getCurrentSheetName();

				SheetReader sheetReader = getSheetReaders()
						.filter(reader -> reader.sheetName.equals(currentSheetName))
						.findFirst()
						.orElseThrow(() -> new IllegalArgumentException("Unknown sheet: " + currentSheetName));

				this.header = getHeader(excelReader);
				sheetReader.readSheet();
			}
		}
	}

	protected abstract InputStream getExcelInputStream();


	protected abstract Stream<SheetReader> getSheetReaders();

	protected InputStream fromResourcePath(String path) {
		return this.getClass().getResourceAsStream(path);
	}

	protected Optional<String> getOptionalString(String col) {
		return ExcelUtil.getOptionalString(col, excelReader, header);
	}

	protected String getString(String col) {
		return ExcelUtil.getString(col, excelReader, header);
	}

	protected OptionalInt getOptionalInteger(String col) {
		return ExcelUtil.getOptionalInteger(col, excelReader, header);
	}

	protected int getInteger(String col) {
		return ExcelUtil.getInteger(col, excelReader, header);
	}

	protected OptionalDouble getOptionalDouble(String col) {
		return ExcelUtil.getOptionalDouble(col, excelReader, header);
	}

	protected double getDouble(String col) {
		return ExcelUtil.getDouble(col, excelReader, header);
	}

	protected Optional<Percent> getOptionalPercent(String col) {
		return ExcelUtil.getOptionalPercent(col, excelReader, header);
	}

	protected Percent getPercent(String col) {
		return ExcelUtil.getPercent(col, excelReader, header);
	}

	protected boolean getBoolean(String col) {
		return ExcelUtil.getBoolean(col, excelReader, header);
	}

	protected Optional<Duration> getOptionalDuration(String col) {
		return ExcelUtil.getOptionalDuration(col, excelReader, header);
	}

	protected Duration getDuration(String col) {
		return ExcelUtil.getDuration(col, excelReader, header);
	}

	protected <T> Optional<T> getOptionalEnum(String col, Function<String, T> producer) {
		return ExcelUtil.getOptionalEnum(col, producer, excelReader, header);
	}

	protected <T> T getEnum(String col, Function<String, T> producer) {
		return ExcelUtil.getEnum(col, producer, excelReader, header);
	}

	protected <T> List<T> getList(String col, Function<String, T> producer) {
		return ExcelUtil.getList(col, producer, excelReader, header);
	}

	protected <T> Set<T> getSet(String col, Function<String, T> producer) {
		return ExcelUtil.getSet(col, producer, excelReader, header);
	}
}

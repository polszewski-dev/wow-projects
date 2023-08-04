package wow.scraper.exporters.excel;

import lombok.Getter;
import lombok.SneakyThrows;
import polszewski.excel.writer.ExcelWriter;
import wow.scraper.config.ScraperConfig;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Getter
public abstract class WowExcelBuilder {
	protected final ScraperConfig config;
	protected final ExcelWriter writer;

	protected WowExcelBuilder(ScraperConfig config) {
		this.config = config;
		this.writer = new ExcelWriter();
	}

	public void start() {
		writer.open();
	}

	@SneakyThrows
	public void finish(String fileName) {
		writer.save(fileName);
	}

	protected <T, B extends WowExcelBuilder> void writeHeader(String sheetName, ExcelSheetWriter<T, B> sheetWriter, int colSplit, int rowSplit) {
		writer.nextSheet(sheetName);
		sheetWriter.writeHeader();
		writer.nextRow().freeze(colSplit, rowSplit);
	}

	protected <T, B extends WowExcelBuilder> void writeRow(T params, ExcelSheetWriter<T, B> sheetWriter) {
		sheetWriter.writeRow(params);
		writer.nextRow();
	}
}

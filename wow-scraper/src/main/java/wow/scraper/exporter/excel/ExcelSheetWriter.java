package wow.scraper.exporter.excel;

import wow.scraper.config.ScraperConfig;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public abstract class ExcelSheetWriter<T, B extends WowExcelBuilder> extends ExcelCellWriter {
	protected final B builder;
	protected final ScraperConfig config;

	protected ExcelSheetWriter(B builder) {
		super(builder.getWriter());
		this.builder = builder;
		this.config = builder.getConfig();
	}

	public abstract void writeHeader();

	public abstract void writeRow(T params);
}

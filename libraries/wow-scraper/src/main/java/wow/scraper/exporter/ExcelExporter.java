package wow.scraper.exporter;

import lombok.Getter;
import wow.scraper.config.ScraperContext;
import wow.scraper.config.ScraperContextSource;
import wow.scraper.exporter.excel.WowExcelBuilder;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Getter
public abstract class ExcelExporter<B extends WowExcelBuilder> implements ScraperContextSource {
	protected ScraperContext scraperContext;
	private B builder;
	private boolean dataPrepared;

	public void init(
			ScraperContext scraperContext,
			B builder
	) {
		this.scraperContext = scraperContext;
		this.builder = builder;
	}

	protected abstract void prepareData();

	protected abstract void exportPreparedData(B builder);

	public final void prepareAndExport() {
		Objects.requireNonNull(builder);
		prepareDataOnce();
		exportPreparedData(builder);
	}

	private void prepareDataOnce() {
		if (!dataPrepared) {
			prepareData();
			this.dataPrepared = true;
		}
	}
}

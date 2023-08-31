package wow.scraper.exporter;

import lombok.Getter;
import wow.scraper.config.ScraperContext;
import wow.scraper.config.ScraperContextSource;
import wow.scraper.exporter.excel.WowExcelBuilder;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Getter
public abstract class ExcelExporter<B extends WowExcelBuilder> implements ScraperContextSource {
	protected ScraperContext scraperContext;
	protected B builder;

	public void init(
			ScraperContext scraperContext,
			B builder
	) {
		this.scraperContext = scraperContext;
		this.builder = builder;
	}

	public abstract void exportAll();
}

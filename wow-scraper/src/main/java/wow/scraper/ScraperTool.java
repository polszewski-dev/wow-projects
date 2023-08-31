package wow.scraper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import wow.scraper.config.ScraperContext;
import wow.scraper.config.ScraperContextSource;
import wow.scraper.exporter.ExcelExporter;
import wow.scraper.exporter.excel.WowExcelBuilder;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
public abstract class ScraperTool implements ScraperContextSource {
	private final ApplicationContext context;

	protected ScraperTool() {
		this.context = new AnnotationConfigApplicationContext("wow.scraper");
	}

	protected abstract void run();

	@Override
	public ScraperContext getScraperContext() {
		return context.getBean(ScraperContext.class);
	}

	@SafeVarargs
	protected final <B extends WowExcelBuilder, E extends ExcelExporter<B>> void exportAll(B builder, E... exporters) {
		for (var exporter : exporters) {
			exporter.init(getScraperContext(), builder);
			exporter.exportAll();
		}
	}
}

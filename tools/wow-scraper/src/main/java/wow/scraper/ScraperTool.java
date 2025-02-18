package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperContext;
import wow.scraper.config.ScraperContextSource;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.exporter.ExcelExporter;
import wow.scraper.exporter.excel.WowExcelBuilder;

import java.io.File;
import java.util.function.BiFunction;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@Slf4j
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

	protected <B extends WowExcelBuilder, E extends ExcelExporter<B>> void export(
			String fileName,
			BiFunction<ScraperConfig, ScraperDatafixes, B> builderFactory,
			E... exporters
	) {
		var config = getScraperConfig();
		var datafixes = getScraperDatafixes();
		var builder = builderFactory.apply(config, datafixes);

		builder.start();

		exportAll(builder, exporters);

		String xlsFilePath = config.getDirectoryPath() + File.pathSeparator + fileName;
		builder.finish(xlsFilePath);

		log.info("Saved to {}", xlsFilePath);
	}

	@SafeVarargs
	protected final <B extends WowExcelBuilder, E extends ExcelExporter<B>> void exportAll(B builder, E... exporters) {
		for (var exporter : exporters) {
			exporter.init(getScraperContext(), builder);
			exporter.prepareAndExport();
		}
	}
}

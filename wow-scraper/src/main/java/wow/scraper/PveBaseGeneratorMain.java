package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.config.ScraperConfig;
import wow.scraper.exporter.ExcelExporter;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.exporter.pve.FactionExporter;
import wow.scraper.exporter.pve.NpcExporter;
import wow.scraper.exporter.pve.ZoneExporter;
import wow.scraper.exporter.pve.excel.FactionExcelBuilder;
import wow.scraper.exporter.pve.excel.NpcExcelBuilder;
import wow.scraper.exporter.pve.excel.ZoneExcelBuilder;

import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Slf4j
public class PveBaseGeneratorMain extends ScraperTool {
	public static void main(String[] args) {
		new PveBaseGeneratorMain().run();
	}

	@Override
	protected void run() {
		export("pve/zones.xls", ZoneExcelBuilder::new, new ZoneExporter());
		export("pve/npcs.xls", NpcExcelBuilder::new, new NpcExporter());
		export("pve/factions.xls", FactionExcelBuilder::new, new FactionExporter());
	}

	protected  <B extends WowExcelBuilder, E extends ExcelExporter<B>> void export(String fileName, Function<ScraperConfig, B> builderFactory, E... exporters) {
		var config = getScraperConfig();
		var builder = builderFactory.apply(config);

		builder.start();

		exportAll(builder, exporters);

		String itemFilePath = config.getDirectoryPath() + "/" + fileName;
		builder.finish(itemFilePath);

		log.info("Saved to {}", itemFilePath);
	}
}

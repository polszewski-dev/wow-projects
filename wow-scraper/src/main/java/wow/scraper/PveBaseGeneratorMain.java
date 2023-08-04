package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.exporters.pve.FactionExporter;
import wow.scraper.exporters.pve.NpcExporter;
import wow.scraper.exporters.pve.ZoneExporter;
import wow.scraper.exporters.pve.excel.PveBaseExcelBuilder;

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
		PveBaseExcelBuilder builder = new PveBaseExcelBuilder(getScraperConfig());
		builder.start();

		exportPveBase(builder);

		String itemFilePath = getScraperConfig().getDirectoryPath() + "/pve_base.xls";
		builder.finish(itemFilePath);

		log.info("Saved to {}", itemFilePath);
	}

	private void exportPveBase(PveBaseExcelBuilder builder) {
		exportAll(
				builder,
				new ZoneExporter(),
				new NpcExporter(),
				new FactionExporter()
		);
	}
}

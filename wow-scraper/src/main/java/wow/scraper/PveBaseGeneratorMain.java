package wow.scraper;

import wow.scraper.exporter.pve.FactionExporter;
import wow.scraper.exporter.pve.NpcExporter;
import wow.scraper.exporter.pve.ZoneExporter;
import wow.scraper.exporter.pve.excel.FactionExcelBuilder;
import wow.scraper.exporter.pve.excel.NpcExcelBuilder;
import wow.scraper.exporter.pve.excel.ZoneExcelBuilder;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
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
}

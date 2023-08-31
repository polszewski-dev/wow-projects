package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.exporter.spell.AbilityExporter;
import wow.scraper.exporter.spell.AbilityRankExporter;
import wow.scraper.exporter.spell.TalentExporter;
import wow.scraper.exporter.spell.TalentRankExporter;
import wow.scraper.exporter.spell.excel.SpellBaseExcelBuilder;

/**
 * User: POlszewski
 * Date: 2023-06-10
 */
@Slf4j
public class SpellBaseGeneratorMain extends ScraperTool {
	public static void main(String[] args) {
		new SpellBaseGeneratorMain().run();
	}

	@Override
	protected void run() {
		SpellBaseExcelBuilder builder = new SpellBaseExcelBuilder(getScraperConfig());
		builder.start();

		exportSpellBase(builder);

		String filePath = getScraperConfig().getDirectoryPath() + "/spell_base.xls";
		builder.finish(filePath);

		log.info("Saved to {}", filePath);
	}

	private void exportSpellBase(SpellBaseExcelBuilder builder) {
		exportAll(
				builder,
				new AbilityExporter(),
				new AbilityRankExporter(),
				new TalentExporter(),
				new TalentRankExporter()
		);
	}
}

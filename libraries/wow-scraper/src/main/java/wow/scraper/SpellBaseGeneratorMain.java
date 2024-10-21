package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.exporter.spell.AbilityExporter;
import wow.scraper.exporter.spell.ItemEffectExporter;
import wow.scraper.exporter.spell.TalentExporter;
import wow.scraper.exporter.spell.excel.SpellBaseExcelBuilder;
import wow.scraper.exporter.spell.excel.TalentExcelBuilder;

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
		export("spell/spells.xls", SpellBaseExcelBuilder::new, new AbilityExporter(), new ItemEffectExporter());
		export("spell/talents.xls", TalentExcelBuilder::new, new TalentExporter());
	}
}

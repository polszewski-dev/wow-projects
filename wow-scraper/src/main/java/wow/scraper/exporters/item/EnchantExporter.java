package wow.scraper.exporters.item;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.parsers.tooltip.EnchantTooltipParser;

import java.io.IOException;

import static wow.scraper.model.WowheadSpellCategory.ENCHANTS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@Slf4j
public class EnchantExporter extends AbstractSpellExporter<EnchantTooltipParser> {
	@Override
	public void exportAll() throws IOException {
		builder.addEnchantHeader();

		export(ENCHANTS);
	}

	@Override
	protected EnchantTooltipParser createParser(JsonSpellDetails details, GameVersionId gameVersion) {
		return new EnchantTooltipParser(details, gameVersion, getStatPatternRepository());
	}

	@Override
	protected void exportParsedData(EnchantTooltipParser parser) {
		builder.add(parser);
	}
}

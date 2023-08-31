package wow.scraper.exporter.item;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.item.excel.ItemBaseExcelBuilder;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.parser.tooltip.EnchantTooltipParser;

import java.util.List;
import java.util.Optional;

import static wow.scraper.model.WowheadSpellCategory.ENCHANTS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class EnchantExporter extends AbstractItemSpellExporter<EnchantTooltipParser> {
	@Override
	protected void prepareData() {
		export(ENCHANTS);
	}

	@Override
	protected void exportPreparedData(ItemBaseExcelBuilder builder) {
		builder.addEnchantHeader();
		parsers.forEach(builder::add);
	}

	@Override
	protected EnchantTooltipParser createParser(JsonSpellDetails details, GameVersionId gameVersion) {
		return new EnchantTooltipParser(details, gameVersion, getScraperContext());
	}

	@Override
	protected List<Integer> getDetailIds(WowheadSpellCategory category, GameVersionId gameVersion) {
		assertOnlyEnchants(category);
		return getSpellDetailRepository().getEnchantDetailIds(gameVersion);
	}

	@Override
	protected Optional<JsonSpellDetails> getDetail(WowheadSpellCategory category, Integer detailId, GameVersionId gameVersion) {
		assertOnlyEnchants(category);
		return getSpellDetailRepository().getEnchantDetail(gameVersion, detailId);
	}

	private static void assertOnlyEnchants(WowheadSpellCategory category) {
		if (category != ENCHANTS) {
			throw new IllegalArgumentException();
		}
	}
}

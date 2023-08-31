package wow.scraper.exporter.item;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.item.excel.ItemBaseExcelBuilder;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.parser.tooltip.GemTooltipParser;

import static wow.scraper.model.WowheadItemCategory.GEMS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class GemItemExporter extends AbstractItemExporter<GemTooltipParser> {
	@Override
	protected void prepareData() {
		export(GEMS);
	}

	@Override
	protected void exportPreparedData(ItemBaseExcelBuilder builder) {
		builder.addGemHeader();
		parsers.forEach(builder::add);
	}

	@Override
	protected GemTooltipParser createParser(JsonItemDetails details, GameVersionId gameVersion) {
		return new GemTooltipParser(details, gameVersion, getScraperContext());
	}
}

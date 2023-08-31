package wow.scraper.exporter.item;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.item.excel.ItemBaseExcelBuilder;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.parser.tooltip.TradedItemParser;

import static wow.scraper.model.WowheadItemCategory.QUEST;
import static wow.scraper.model.WowheadItemCategory.TOKENS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class TradedItemExporter extends AbstractItemExporter<TradedItemParser> {
	@Override
	protected void prepareData() {
		export(TOKENS);
		export(QUEST);
	}

	@Override
	protected void exportPreparedData(ItemBaseExcelBuilder builder) {
		builder.addTradedItemHeader();
		parsers.forEach(builder::add);
	}

	@Override
	protected TradedItemParser createParser(JsonItemDetails details, GameVersionId gameVersion) {
		return new TradedItemParser(details, gameVersion, getScraperContext());
	}
}

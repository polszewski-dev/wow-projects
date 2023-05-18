package wow.scraper.exporters.item;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.parsers.tooltip.TradedItemParser;

import java.io.IOException;

import static wow.scraper.model.WowheadItemCategory.QUEST;
import static wow.scraper.model.WowheadItemCategory.TOKENS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class TradedItemExporter extends ItemBaseExporter<TradedItemParser> {
	@Override
	public void exportAll() throws IOException {
		builder.addTradedItemHeader();
		export(TOKENS);
		export(QUEST);
	}

	@Override
	protected TradedItemParser createParser(JsonItemDetails itemDetails, GameVersionId gameVersion) {
		return new TradedItemParser(itemDetails, gameVersion, statPatternRepository);
	}

	@Override
	protected void exportParsedData(TradedItemParser parser) {
		builder.add(parser);
	}
}

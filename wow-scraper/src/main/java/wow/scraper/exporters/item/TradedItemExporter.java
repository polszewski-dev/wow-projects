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
public class TradedItemExporter extends AbstractItemExporter<TradedItemParser> {
	@Override
	public void exportAll() throws IOException {
		builder.addTradedItemHeader();
		export(TOKENS);
		export(QUEST);
	}

	@Override
	protected TradedItemParser createParser(JsonItemDetails details, GameVersionId gameVersion) {
		return new TradedItemParser(details, gameVersion, statPatternRepository);
	}

	@Override
	protected void exportParsedData(TradedItemParser parser) {
		builder.add(parser);
	}
}
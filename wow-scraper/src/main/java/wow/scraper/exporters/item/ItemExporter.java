package wow.scraper.exporters.item;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parsers.tooltip.ItemTooltipParser;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class ItemExporter extends AbstractItemExporter<ItemTooltipParser> {
	@Override
	public void exportAll() throws IOException {
		builder.addItemHeader();

		for (WowheadItemCategory category : WowheadItemCategory.equipment()) {
			export(category);
		}
	}

	@Override
	protected ItemTooltipParser createParser(JsonItemDetails details, GameVersionId gameVersion) {
		return new ItemTooltipParser(details, gameVersion, statPatternRepository);
	}

	@Override
	protected void exportParsedData(ItemTooltipParser parser) {
		builder.add(parser);
	}
}

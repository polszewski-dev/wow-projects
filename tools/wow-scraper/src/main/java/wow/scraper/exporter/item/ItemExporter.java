package wow.scraper.exporter.item;

import lombok.RequiredArgsConstructor;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.item.excel.ItemExcelBuilder;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parser.tooltip.ItemTooltipParser;

import static wow.scraper.model.WowheadItemCategory.EquipmentGroup;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@RequiredArgsConstructor
public class ItemExporter extends AbstractItemExporter<ItemTooltipParser, ItemExcelBuilder> {
	private final EquipmentGroup equipmentGroup;

	@Override
	protected void prepareData() {
		for (var category : WowheadItemCategory.withGroup(equipmentGroup)) {
			export(category);
		}
	}

	@Override
	protected void exportPreparedData(ItemExcelBuilder builder) {
		builder.addItemHeader();
		parsers.forEach(builder::add);
		parsers.forEach(ItemSetExporter.SAVED_SETS::save);
	}

	@Override
	protected ItemTooltipParser createParser(JsonItemDetails details, GameVersionId gameVersion) {
		return new ItemTooltipParser(details, gameVersion, getScraperContext());
	}
}

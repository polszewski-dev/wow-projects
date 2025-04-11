package wow.minmax.repository.impl.parser.config;

import wow.character.model.equipment.ItemLevelFilter;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.minmax.model.config.ItemLevelConfig;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.REQ_LEVEL;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public class ItemLevelFilterSheetParser extends WowExcelSheetParser {
	private final MinMaxConfigExcelParser parser;

	protected ItemLevelFilterSheetParser(String sheetName, MinMaxConfigExcelParser parser) {
		super(sheetName);
		this.parser = parser;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return column(REQ_LEVEL);
	}

	@Override
	protected void readSingleRow() {
		var itemLevelConfig = getItemLevelConfig();
		parser.add(itemLevelConfig);
	}

	private ItemLevelConfig getItemLevelConfig() {
		var characterRestriction = getRestriction();
		var timeRestriction = getTimeRestriction();
		var itemLevelFilter = getItemLevelFilter();

		return new ItemLevelConfig(
				characterRestriction, timeRestriction, itemLevelFilter
		);
	}

	private ItemLevelFilter getItemLevelFilter() {
		var itemLevelFilter = new ItemLevelFilter();

		for (var itemRarity : ItemRarity.values()) {
			var colMinILvl = column("min ilvl: " + itemRarity.name().toLowerCase(), true);
			var minItemLevel = colMinILvl.getNullableInteger();

			if (minItemLevel != null) {
				itemLevelFilter.setMinItemLevel(itemRarity, minItemLevel);
			}
		}

		return itemLevelFilter;
	}
}

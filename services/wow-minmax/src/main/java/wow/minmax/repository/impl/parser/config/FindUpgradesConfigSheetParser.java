package wow.minmax.repository.impl.parser.config;

import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.minmax.model.config.FindUpgradesConfig;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public class FindUpgradesConfigSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colEnchants = column("enchants");

	private final MinMaxConfigExcelParser parser;

	protected FindUpgradesConfigSheetParser(String sheetName, MinMaxConfigExcelParser parser) {
		super(sheetName);
		this.parser = parser;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colEnchants;
	}

	@Override
	protected void readSingleRow() {
		var config = getConfig();
		parser.add(config);
	}

	private FindUpgradesConfig getConfig() {
		var characterRestriction = getRestriction();
		var timeRestriction = getTimeRestriction();
		var enchantNames = colEnchants.getSet(x -> x);

		return new FindUpgradesConfig(
				characterRestriction, timeRestriction, enchantNames
		);
	}
}

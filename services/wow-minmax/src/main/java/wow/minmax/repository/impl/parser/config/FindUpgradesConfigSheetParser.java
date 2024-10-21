package wow.minmax.repository.impl.parser.config;

import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.minmax.model.config.FindUpgradesConfig;
import wow.minmax.repository.impl.MinmaxConfigRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public class FindUpgradesConfigSheetParser extends WowExcelSheetParser {
	private final MinmaxConfigRepositoryImpl configRepository;

	private final ExcelColumn colEnchants = column("enchants");

	protected FindUpgradesConfigSheetParser(String sheetName, MinmaxConfigRepositoryImpl configRepository) {
		super(sheetName);
		this.configRepository = configRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colEnchants;
	}

	@Override
	protected void readSingleRow() {
		FindUpgradesConfig config = getConfig();
		configRepository.add(config);
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

package wow.minmax.repository.impl.parsers.config;

import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;
import wow.minmax.model.config.FindUpgradesConfig;
import wow.minmax.repository.impl.MinmaxConfigRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public class FindUpgradesConfigSheetParser extends WowExcelSheetParser {
	private final MinmaxConfigRepositoryImpl configRepository;

	private final ExcelColumn colClass = column("class");
	private final ExcelColumn colRole = column("role");
	private final ExcelColumn colVersion = column("version");
	private final ExcelColumn colEnchants = column("enchants");

	protected FindUpgradesConfigSheetParser(String sheetName, MinmaxConfigRepositoryImpl configRepository) {
		super(sheetName);
		this.configRepository = configRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colClass;
	}

	@Override
	protected void readSingleRow() {
		FindUpgradesConfig config = getConfig();
		configRepository.add(config);
	}

	private FindUpgradesConfig getConfig() {
		var characterClassId = colClass.getEnum(CharacterClassId::parse);
		var pveRole = colRole.getEnum(PveRole::parse);
		var gameVersionId = colVersion.getEnum(GameVersionId::parse);
		var enchantNames = colEnchants.getSet(x -> x);

		return new FindUpgradesConfig(
				characterClassId, pveRole, gameVersionId, enchantNames
		);
	}
}

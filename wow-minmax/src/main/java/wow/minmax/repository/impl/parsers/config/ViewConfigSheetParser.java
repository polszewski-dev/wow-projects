package wow.minmax.repository.impl.parsers.config;

import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.repository.impl.MinmaxConfigRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public class ViewConfigSheetParser extends WowExcelSheetParser {
	private final MinmaxConfigRepositoryImpl configRepository;

	private final ExcelColumn colClass = column("class");
	private final ExcelColumn colRole = column("role");
	private final ExcelColumn colVersion = column("version");
	private final ExcelColumn colRelevantSpells = column("relevant_spells");

	protected ViewConfigSheetParser(String sheetName, MinmaxConfigRepositoryImpl configRepository) {
		super(sheetName);
		this.configRepository = configRepository;
	}

	@Override
	protected WowExcelSheetParser.ExcelColumn getColumnIndicatingOptionalRow() {
		return colClass;
	}

	@Override
	protected void readSingleRow() {
		ViewConfig viewConfig = getViewConfig();
		configRepository.add(viewConfig);
	}

	private ViewConfig getViewConfig() {
		var characterClassId = colClass.getEnum(CharacterClassId::parse);
		var pveRole = colRole.getEnum(PveRole::parse);
		var gameVersionId = colVersion.getEnum(GameVersionId::parse);
		var relevantSpells = colRelevantSpells.getList(SpellId::parse);

		return new ViewConfig(
				characterClassId, pveRole, gameVersionId, relevantSpells
		);
	}
}

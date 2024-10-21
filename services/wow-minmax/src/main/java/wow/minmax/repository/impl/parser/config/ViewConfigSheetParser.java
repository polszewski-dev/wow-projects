package wow.minmax.repository.impl.parser.config;

import wow.commons.model.spell.AbilityId;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.repository.impl.MinmaxConfigRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public class ViewConfigSheetParser extends WowExcelSheetParser {
	private final MinmaxConfigRepositoryImpl configRepository;

	private final ExcelColumn colEqvAmount = column("eqv_amount");
	private final ExcelColumn colRelevantSpells = column("relevant_spells");

	protected ViewConfigSheetParser(String sheetName, MinmaxConfigRepositoryImpl configRepository) {
		super(sheetName);
		this.configRepository = configRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colRelevantSpells;
	}

	@Override
	protected void readSingleRow() {
		ViewConfig viewConfig = getViewConfig();
		configRepository.add(viewConfig);
	}

	private ViewConfig getViewConfig() {
		var characterRestriction = getRestriction();
		var timeRestriction = getTimeRestriction();
		var eqvAmount = colEqvAmount.getDouble();
		var relevantSpells = colRelevantSpells.getList(AbilityId::parse);

		return new ViewConfig(
				characterRestriction, timeRestriction, eqvAmount, relevantSpells
		);
	}
}

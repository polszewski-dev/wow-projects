package wow.minmax.repository.impl.parser.config;

import wow.commons.model.spell.AbilityId;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.minmax.model.config.ViewConfig;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public class ViewConfigSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colEqvAmount = column("eqv_amount");
	private final ExcelColumn colRelevantSpells = column("relevant_spells");

	private final MinMaxConfigExcelParser parser;

	protected ViewConfigSheetParser(String sheetName, MinMaxConfigExcelParser parser) {
		super(sheetName);
		this.parser = parser;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colRelevantSpells;
	}

	@Override
	protected void readSingleRow() {
		var viewConfig = getViewConfig();
		parser.add(viewConfig);
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

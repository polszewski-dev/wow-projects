package wow.minmax.repository.impl.parser.config;

import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.minmax.model.config.CharacterFeature;
import wow.minmax.model.config.CharacterFeatureConfig;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public class CharacterFeatureConfigSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colFeature = column("feature");

	private final MinMaxConfigExcelParser parser;

	protected CharacterFeatureConfigSheetParser(String sheetName, MinMaxConfigExcelParser parser) {
		super(sheetName);
		this.parser = parser;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colFeature;
	}

	@Override
	protected void readSingleRow() {
		var characterFeatureConfig = getCharacterFeatureConfig();
		parser.add(characterFeatureConfig);
	}

	private CharacterFeatureConfig getCharacterFeatureConfig() {
		var characterRestriction = getRestriction();
		var timeRestriction = getTimeRestriction();
		var feature = colFeature.getEnum(CharacterFeature::parse);

		return new CharacterFeatureConfig(
				characterRestriction, timeRestriction, feature
		);
	}
}

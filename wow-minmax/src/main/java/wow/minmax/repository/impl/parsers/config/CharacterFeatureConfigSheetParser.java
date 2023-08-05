package wow.minmax.repository.impl.parsers.config;

import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;
import wow.minmax.model.config.CharacterFeature;
import wow.minmax.model.config.CharacterFeatureConfig;
import wow.minmax.repository.impl.MinmaxConfigRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public class CharacterFeatureConfigSheetParser extends WowExcelSheetParser {
	private final MinmaxConfigRepositoryImpl configRepository;

	private final ExcelColumn colFeature = column("feature");

	protected CharacterFeatureConfigSheetParser(String sheetName, MinmaxConfigRepositoryImpl configRepository) {
		super(sheetName);
		this.configRepository = configRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colFeature;
	}

	@Override
	protected void readSingleRow() {
		CharacterFeatureConfig characterFeatureConfig = getCharacterFeatureConfig();
		configRepository.add(characterFeatureConfig);
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

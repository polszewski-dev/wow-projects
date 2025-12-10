package wow.character.repository.impl.parser.character;

import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

import static wow.character.model.asset.AssetOption.ExclusionGroup;
import static wow.character.model.asset.AssetOption.SingleOption;
import static wow.commons.util.CollectionUtil.getUniqueResult;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public class AssetTemplateOptionSheetParser extends WowExcelSheetParser {
	protected final ExcelColumn colDefault = column("default");
	protected final ExcelColumn colExclusionGroup = column("exclusion group");
	protected final ExcelColumn colTalentName = column("talent name");
	protected final ExcelColumn colBuffPhaseScript = column("buff phase script");
	protected final ExcelColumn colWarmUpPhaseScript = column("warm up phase script");
	protected final ExcelColumn colSpecificToAssets = column("specific to assets");

	private final AssetTemplateExcelParser parser;

	public AssetTemplateOptionSheetParser(String sheetName, AssetTemplateExcelParser parser) {
		super(sheetName);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var assetTemplateOption = getAssetTemplateOption();
		var gameVersionId = getTimeRestriction().getGameVersionId();
		var characterClassId = getUniqueResult(getRestriction().characterClassIds()).orElseThrow();
		var specificToAssets = colSpecificToAssets.getList(x -> x);

		parser.addAssetTemplateOption(assetTemplateOption, characterClassId, gameVersionId, specificToAssets);
	}

	private SingleOption getAssetTemplateOption() {
		var name = colName.getString();
		var isDefault = colDefault.getBoolean();
		var exclusionGroup = colExclusionGroup.getEnum(ExclusionGroup::parse, null);
		var talentName = colTalentName.getString(null);
		var buffPhaseScript = colBuffPhaseScript.getString(null);
		var warmUpPhaseScript = colWarmUpPhaseScript.getString(null);

		return new SingleOption(
				name,
				isDefault,
				exclusionGroup,
				talentName,
				buffPhaseScript,
				warmUpPhaseScript
		);
	}
}

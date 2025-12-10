package wow.character.repository.impl.parser.character;

import wow.character.model.asset.AssetTemplate;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

import java.util.List;

import static wow.commons.util.CollectionUtil.getUniqueResult;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public class AssetTemplateSheetParser extends WowExcelSheetParser {
	private final AssetTemplateExcelParser parser;

	public AssetTemplateSheetParser(String sheetName, AssetTemplateExcelParser parser) {
		super(sheetName);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var assetTemplate = getAssetTemplate();
		parser.addAssetTemplate(assetTemplate);
	}

	private AssetTemplate getAssetTemplate() {
		var name = colName.getString();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();

		return new AssetTemplate(
				name,
				getUniqueResult(characterRestriction.characterClassIds()).orElseThrow(),
				timeRestriction.getGameVersionId(),
				List.of()
		);
	}
}

package wow.character.repository.impl.parser.character;

import wow.character.model.asset.Asset;
import wow.character.model.asset.AssetId;
import wow.commons.model.spell.AbilityId;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

import static wow.character.model.asset.Asset.*;
import static wow.character.model.script.ScriptPathResolver.requireExistingScriptFile;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public class AssetSheetParser extends WowExcelSheetParser {
	protected final ExcelColumn colImprovedByTalent = column("improved by talent");
	protected final ExcelColumn colScope = column("scope");
	protected final ExcelColumn colExclusionGroup = column("exclusion group");
	protected final ExcelColumn colBuffTarget = column("buff target");
	protected final ExcelColumn colBuffAbility = column("buff ability");
	protected final ExcelColumn colBuffScript = column("buff script");

	private final AssetExcelParser parser;

	public AssetSheetParser(String sheetName, AssetExcelParser parser) {
		super(sheetName);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var asset = getAsset();

		parser.addAsset(asset);
	}

	private Asset getAsset() {
		var id = colId.getInteger(AssetId::of);
		var improvedByTalent = colImprovedByTalent.getString(null);
		var scope = colScope.getEnum(Scope::parse);
		var exclusionGroup = colExclusionGroup.getEnum(ExclusionGroup::parse, null);
		var buffCommand = getBuffCommand();
		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();

		return new Asset(
				id,
				description,
				timeRestriction,
				characterRestriction,
				improvedByTalent,
				scope,
				exclusionGroup,
				buffCommand
		);
	}

	private BuffCommand getBuffCommand() {
		var buffTarget = colBuffTarget.getEnum(BuffTarget::parse, null);

		if (buffTarget == null) {
			return null;
		}

		if (colBuffScript.isEmpty()) {
			var buffAbility = colBuffAbility.getEnum(AbilityId::of);

			return new CastAbility(buffTarget, buffAbility);
		}

		var buffScript = insideAssetDirectory(colBuffScript.getString());

		requireExistingScriptFile(buffScript, getTimeRestriction().getGameVersionId());

		return new ExecuteScript(buffTarget, buffScript);
	}

	private String insideAssetDirectory(String fileName) {
		if (fileName == null) {
			return null;
		}
		return "asset/" + fileName;
	}
}

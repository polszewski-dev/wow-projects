package wow.character.repository.impl.parser.character;

import wow.character.model.character.GameVersion;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.pve.GameVersionId;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class GameVersionSheetParser extends CharacterSheetParser {
	private final ExcelColumn colBasePveSpellHitChance = column("base_pve_spell_hit_chances");
	private final ExcelColumn colMaxPveSpellHitChance = column("max_pve_spell_hit_chance");

	public GameVersionSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colId;
	}

	@Override
	protected void readSingleRow() {
		GameVersion gameVersion = getGameVersion();
		characterRepository.addGameVersion(gameVersion);
	}

	private GameVersion getGameVersion() {
		var id = colId.getEnum(GameVersionId::parse);
		var description = getDescription();
		var basePveSpellHitChances = colBasePveSpellHitChance.getList(Double::valueOf, ";");
		var maxPveSpellHitChance = colMaxPveSpellHitChance.getDouble();

		return new GameVersion(
				id, description, basePveSpellHitChances, maxPveSpellHitChance
		);
	}
}

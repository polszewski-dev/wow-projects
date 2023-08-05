package wow.character.repository.impl.parsers.character;

import wow.character.model.character.GameVersion;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.pve.GameVersionId;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class GameVersionSheetParser extends CharacterSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colCombatRatings = column("combat_ratings");
	private final ExcelColumn colEqvAmount = column("eqv_amount");
	private final ExcelColumn colWorldBuffs = column("world_buffs");
	private final ExcelColumn colGems = column("gems");
	private final ExcelColumn colGlyphs = column("glyphs");
	private final ExcelColumn colHeroics = column("heroics");
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
		var combatRatings = colCombatRatings.getBoolean();
		var eqvAmount = colEqvAmount.getDouble();
		var worldBuffs = colWorldBuffs.getBoolean();
		var gems = colGems.getBoolean();
		var glyphs = colGlyphs.getBoolean();
		var heroics = colHeroics.getBoolean();
		var basePveSpellHitChances = colBasePveSpellHitChance.getList(Double::valueOf, ";");
		var maxPveSpellHitChance = colMaxPveSpellHitChance.getDouble();

		return new GameVersion(
				id, description, combatRatings, eqvAmount, worldBuffs, gems, glyphs, heroics, basePveSpellHitChances, maxPveSpellHitChance
		);
	}
}

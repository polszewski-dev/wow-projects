package wow.commons.repository.impl.parser.pve;

import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.commons.repository.impl.pve.GameVersionRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class GameVersionSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colBasePveSpellHitChance = column("base_pve_spell_hit_chances");
	private final ExcelColumn colMaxPveSpellHitChance = column("max_pve_spell_hit_chance");

	private final GameVersionRepositoryImpl gameVersionRepository;

	public GameVersionSheetParser(String sheetName, GameVersionRepositoryImpl gameVersionRepository) {
		super(sheetName);
		this.gameVersionRepository = gameVersionRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colId;
	}

	@Override
	protected void readSingleRow() {
		GameVersion gameVersion = getGameVersion();
		gameVersionRepository.addGameVersion(gameVersion);
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

package wow.character.repository.impl.parser.character;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.character.model.character.BaseStatInfo;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.pve.GameVersionRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
public class BaseStatInfoExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final GameVersionRepository gameVersionRepository;

	private record BaseKey(CharacterClassId characterClassId, RaceId raceId, int level, GameVersionId gameVersionId) {}
	
	private record IncrementKey(CharacterClassId characterClassId, int level, GameVersionId gameVersionId) {}
	
	private final Map<BaseKey, BaseStatInfo> bases = new HashMap<>();
	private final Map<IncrementKey, BaseStatInfo> increments = new HashMap<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new BaseStatInfoSheetParser("base_stats", gameVersionRepository, this::addBase),
				new BaseStatInfoSheetParser("stat_increments", gameVersionRepository, this::addIncrement)
		);
	}

	private void addBase(BaseStatInfo baseStatInfo) {
		var key = new BaseKey(
				baseStatInfo.getCharacterClassId(),
				baseStatInfo.getRaceId(),
				baseStatInfo.getLevel(),
				baseStatInfo.getGameVersionId()
		);
		bases.put(key, baseStatInfo);
	}

	private void addIncrement(BaseStatInfo baseStatInfo) {
		var key = new IncrementKey(
				baseStatInfo.getCharacterClassId(),
				baseStatInfo.getLevel(),
				baseStatInfo.getGameVersionId()
		);
		increments.put(key, baseStatInfo);
	}

	public List<BaseStatInfo> getBaseStatInfos() {
		var result = new ArrayList<BaseStatInfo>();

		for (var base : bases.values()) {
			int maxLevel = base.getGameVersion().getLastPhase().getMaxLevel();
			var current = base;

			result.add(current);
			
			for (int level = 2; level <= maxLevel; ++level) {
				var key = new IncrementKey(current.getCharacterClassId(), level, current.getGameVersionId());
				var increment = increments.get(key);
				current = incrementBy(current, increment);
				result.add(current);
			}
		}
		
		return result;
	}

	private BaseStatInfo incrementBy(BaseStatInfo current, BaseStatInfo increment) {
		return new BaseStatInfo(
				current.getLevel() + 1,
				current.getCharacterClass(),
				current.getRace(),
				current.getBaseStrength() + increment.getBaseStrength(),
				current.getBaseAgility() + increment.getBaseAgility(),
				current.getBaseStamina() + increment.getBaseStamina(),
				current.getBaseIntellect() + increment.getBaseIntellect(),
				current.getBaseSpirit() + increment.getBaseSpirit(),
				current.getBaseHealth() + increment.getBaseHealth(),
				current.getBaseMana() + increment.getBaseMana(),
				current.getBaseSpellCritPct(),
				increment.getIntellectPerCritPct(),
				current.getGameVersion()
		);
	}
}

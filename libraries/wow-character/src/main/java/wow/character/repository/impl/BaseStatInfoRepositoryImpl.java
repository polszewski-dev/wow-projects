package wow.character.repository.impl;

import org.springframework.stereotype.Component;
import wow.character.model.character.BaseStatInfo;
import wow.character.repository.BaseStatInfoRepository;
import wow.character.repository.impl.parser.character.BaseStatInfoExcelParser;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.util.GameVersionMap;

import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
public class BaseStatInfoRepositoryImpl implements BaseStatInfoRepository {
	private record Key(CharacterClassId characterClassId, RaceId raceId, int level) {}

	private final GameVersionMap<Key, BaseStatInfo> baseStatInfoByKey = new GameVersionMap<>();

	public BaseStatInfoRepositoryImpl(BaseStatInfoExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getBaseStatInfos().forEach(this::addBaseStatInfo);
	}

	@Override
	public Optional<BaseStatInfo> getBaseStatInfo(GameVersionId gameVersionId, CharacterClassId characterClassId, RaceId raceId, int level) {
		var key = new Key(characterClassId, raceId, level);
		return baseStatInfoByKey.getOptional(gameVersionId, key);
	}

	private void addBaseStatInfo(BaseStatInfo baseStatInfo) {
		var key = new Key(
				baseStatInfo.getCharacterClassId(),
				baseStatInfo.getRaceId(),
				baseStatInfo.getLevel()
		);
		baseStatInfoByKey.put(baseStatInfo.getGameVersionId(), key, baseStatInfo);
	}
}

package wow.character.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.character.model.character.BaseStatInfo;
import wow.character.repository.BaseStatInfoRepository;
import wow.character.repository.impl.parser.character.BaseStatInfoExcelParser;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.pve.GameVersionRepository;
import wow.commons.util.GameVersionMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Repository
@RequiredArgsConstructor
public class BaseStatInfoRepositoryImpl implements BaseStatInfoRepository {
	private final GameVersionMap<String, BaseStatInfo> baseStatInfoByKey = new GameVersionMap<>();

	private final GameVersionRepository gameVersionRepository;

	@Override
	public Optional<BaseStatInfo> getBaseStatInfo(GameVersionId gameVersionId, CharacterClassId characterClassId, RaceId raceId, int level) {
		var key = getBaseStatInfoKey(characterClassId, raceId, level);
		return baseStatInfoByKey.getOptional(gameVersionId, key);
	}

	@Value("${base.stat.infos.xls.file.path}")
	private String xlsFilePath;

	@PostConstruct
	public void init() throws IOException {
		var excelParser = new BaseStatInfoExcelParser(xlsFilePath, gameVersionRepository, this);
		excelParser.readFromXls();
	}

	public void addBaseStatInfo(BaseStatInfo baseStatInfo) {
		var key = getBaseStatInfoKey(baseStatInfo);
		baseStatInfoByKey.put(baseStatInfo.getGameVersionId(), key, baseStatInfo);
	}

	private static String getBaseStatInfoKey(BaseStatInfo baseStatInfo) {
		return getBaseStatInfoKey(baseStatInfo.getCharacterClassId(), baseStatInfo.getRaceId(), baseStatInfo.getLevel());
	}

	private static String getBaseStatInfoKey(CharacterClassId characterClassId, RaceId raceId, int level) {
		return characterClassId + "#" + level + "#" + raceId;
	}
}

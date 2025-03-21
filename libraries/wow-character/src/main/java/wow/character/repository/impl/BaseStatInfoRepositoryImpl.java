package wow.character.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
@Component
@RequiredArgsConstructor
public class BaseStatInfoRepositoryImpl implements BaseStatInfoRepository {
	private record Key(CharacterClassId characterClassId, RaceId raceId, int level) {}

	private final GameVersionMap<Key, BaseStatInfo> baseStatInfoByKey = new GameVersionMap<>();

	private final GameVersionRepository gameVersionRepository;

	@Override
	public Optional<BaseStatInfo> getBaseStatInfo(GameVersionId gameVersionId, CharacterClassId characterClassId, RaceId raceId, int level) {
		var key = new Key(characterClassId, raceId, level);
		return baseStatInfoByKey.getOptional(gameVersionId, key);
	}

	@Value("${base.stat.infos.xls.file.path}")
	private String xlsFilePath;

	@PostConstruct
	public void init() throws IOException {
		var excelParser = new BaseStatInfoExcelParser(xlsFilePath, gameVersionRepository);
		excelParser.readFromXls();
		excelParser.getBaseStatInfos().forEach(this::addBaseStatInfo);
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

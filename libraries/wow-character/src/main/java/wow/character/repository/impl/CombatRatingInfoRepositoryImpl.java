package wow.character.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.character.model.character.CombatRatingInfo;
import wow.character.repository.CombatRatingInfoRepository;
import wow.character.repository.impl.parser.character.CombatRatingInfoExcelParser;
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
public class CombatRatingInfoRepositoryImpl implements CombatRatingInfoRepository {
	private final GameVersionMap<Integer, CombatRatingInfo> combatRatingInfoByLevel = new GameVersionMap<>();

	private final GameVersionRepository gameVersionRepository;

	@Override
	public Optional<CombatRatingInfo> getCombatRatingInfo(GameVersionId gameVersionId, int level) {
		return combatRatingInfoByLevel.getOptional(gameVersionId, level);
	}

	@Value("${combat.rating.infos.xls.file.path}")
	private String xlsFilePath;

	@PostConstruct
	public void init() throws IOException {
		var excelParser = new CombatRatingInfoExcelParser(xlsFilePath, gameVersionRepository, this);
		excelParser.readFromXls();
	}

	public void addCombatRatingInfo(CombatRatingInfo combatRatingInfo) {
		combatRatingInfoByLevel.put(combatRatingInfo.getGameVersion().getGameVersionId(), combatRatingInfo.getLevel(), combatRatingInfo);
	}
}

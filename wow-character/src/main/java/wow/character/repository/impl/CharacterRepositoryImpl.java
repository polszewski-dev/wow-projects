package wow.character.repository.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CombatRatingInfo;
import wow.character.repository.CharacterRepository;
import wow.character.repository.impl.parsers.character.CharacterExcelParser;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.ExcelRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Repository
@RequiredArgsConstructor
public class CharacterRepositoryImpl extends ExcelRepository implements CharacterRepository {
	private final Map<String, List<BaseStatInfo>> baseStatInfos = new HashMap<>();
	private final Map<String, List<CombatRatingInfo>> combatRatingInfos = new HashMap<>();
	private final Map<String, List<BuildTemplate>> buildTemplateByKey = new HashMap<>();

	@Value("${character.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<BaseStatInfo> getBaseStats(CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId) {
		String key = getBaseStatInfoKey(characterClassId, raceId, level);
		return getUnique(baseStatInfos, key, phaseId);
	}

	@Override
	public Optional<CombatRatingInfo> getCombatRatings(int level, PhaseId phaseId) {
		String key = getCombatRatingInfoKey(level);
		return getUnique(combatRatingInfos, key, phaseId);
	}

	@Override
	public Optional<BuildTemplate> getBuildTemplate(BuildId buildId, CharacterClassId characterClassId, int level, PhaseId phaseId) {
		String key = getBuildTemplateKey(buildId, characterClassId, level);
		return getUnique(buildTemplateByKey, key, phaseId);
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var excelParser = new CharacterExcelParser(xlsFilePath, this);
		excelParser.readFromXls();
	}

	public void addBaseStatInfo(BaseStatInfo baseStatInfo) {
		String key = getBaseStatInfoKey(baseStatInfo.getCharacterClassId(), baseStatInfo.getRaceId(), baseStatInfo.getLevel());
		addEntry(baseStatInfos, key, baseStatInfo);
	}

	public void addCombatRatingInfo(CombatRatingInfo combatRatingInfo) {
		String key = getCombatRatingInfoKey(combatRatingInfo.getLevel());
		addEntry(combatRatingInfos, key, combatRatingInfo);
	}

	public void addBuildTemplate(BuildTemplate buildTemplate) {
		String key = getBuildTemplateKey(buildTemplate.getBuildId(), buildTemplate.getCharacterClassId(), buildTemplate.getLevel());
		addEntry(buildTemplateByKey, key, buildTemplate);
	}

	private static String getBaseStatInfoKey(CharacterClassId characterClassId, RaceId raceId, int level) {
		return characterClassId + "#" + raceId + "#" + level;
	}

	private static String getCombatRatingInfoKey(int level) {
		return level + "";
	}

	private static String getBuildTemplateKey(BuildId buildId, CharacterClassId characterClassId, int level) {
		return buildId + "#" + characterClassId + "#" + level;
	}
}

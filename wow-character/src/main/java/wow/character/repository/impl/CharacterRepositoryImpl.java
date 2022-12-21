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
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
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
	public Optional<BaseStatInfo> getBaseStats(CharacterClass characterClass, Race race, int level, Phase phase) {
		String key = getBaseStatInfoKey(characterClass, race, level);
		return getUnique(baseStatInfos, key, phase);
	}

	@Override
	public Optional<CombatRatingInfo> getCombatRatings(int level, Phase phase) {
		String key = getCombatRatingInfoKey(level);
		return getUnique(combatRatingInfos, key, phase);
	}

	@Override
	public Optional<BuildTemplate> getBuildTemplate(BuildId buildId, CharacterClass characterClass, int level, Phase phase) {
		String key = getBuildTemplateKey(buildId, characterClass, level);
		return getUnique(buildTemplateByKey, key, phase);
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var excelParser = new CharacterExcelParser(xlsFilePath, this);
		excelParser.readFromXls();
	}

	public void addBaseStatInfo(BaseStatInfo baseStatInfo) {
		String key = getBaseStatInfoKey(baseStatInfo.getCharacterClass(), baseStatInfo.getRace(), baseStatInfo.getLevel());
		addEntry(baseStatInfos, key, baseStatInfo);
	}

	public void addCombatRatingInfo(CombatRatingInfo combatRatingInfo) {
		String key = getCombatRatingInfoKey(combatRatingInfo.getLevel());
		addEntry(combatRatingInfos, key, combatRatingInfo);
	}

	public void addBuildTemplate(BuildTemplate buildTemplate) {
		String key = getBuildTemplateKey(buildTemplate.getBuildId(), buildTemplate.getCharacterClass(), buildTemplate.getLevel());
		addEntry(buildTemplateByKey, key, buildTemplate);
	}

	private static String getBaseStatInfoKey(CharacterClass characterClass, Race race, int level) {
		return characterClass + "#" + race + "#" + level;
	}

	private static String getCombatRatingInfoKey(int level) {
		return level + "";
	}

	private static String getBuildTemplateKey(BuildId buildId, CharacterClass characterClass, int level) {
		return buildId + "#" + characterClass + "#" + level;
	}
}

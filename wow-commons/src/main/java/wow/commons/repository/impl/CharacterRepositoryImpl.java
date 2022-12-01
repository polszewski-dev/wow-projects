package wow.commons.repository.impl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Repository;
import wow.commons.model.character.*;
import wow.commons.repository.CharacterRepository;
import wow.commons.repository.impl.parsers.character.CharacterExcelParser;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Repository
public class CharacterRepositoryImpl implements CharacterRepository {
	private final List<BaseStatInfo> baseStatInfos = new ArrayList<>();
	private final List<CombatRatingInfo> combatRatingInfos = new ArrayList<>();
	private final Map<String, BuildTemplate> buildTemplateByIdByLevel = new HashMap<>();

	@Override
	public Optional<BaseStatInfo> getBaseStats(CharacterClass characterClass, Race race, int level) {
		return baseStatInfos.stream()
				.filter(x -> x.getCharacterClass() == characterClass && x.getRace() == race && x.getLevel() == level)
				.findAny();
	}

	@Override
	public Optional<CombatRatingInfo> getCombatRatings(int level) {
		return combatRatingInfos.stream()
				.filter(x -> x.getLevel() == level)
				.findAny();
	}

	@Override
	public Optional<BuildTemplate> getBuildTemplate(BuildId buildId, CharacterClass characterClass, int level) {
		String key = getKey(buildId, characterClass, level);
		return Optional.ofNullable(buildTemplateByIdByLevel.get(key));
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var excelParser = new CharacterExcelParser(this);
		excelParser.readFromXls();
	}

	public void addBaseStatInfo(BaseStatInfo baseStatInfo) {
		baseStatInfos.add(baseStatInfo);
	}

	public void addCombatRatingInfo(CombatRatingInfo combatRatingInfo) {
		combatRatingInfos.add(combatRatingInfo);
	}

	public void addBuildTemplate(BuildTemplate buildTemplate) {
		String key = getKey(buildTemplate.getBuildId(), buildTemplate.getCharacterClass(), buildTemplate.getLevel());
		buildTemplateByIdByLevel.put(key, buildTemplate);
	}

	private static String getKey(BuildId buildId, CharacterClass characterClass, int level) {
		return buildId + "#" + characterClass + "#" + level;
	}
}

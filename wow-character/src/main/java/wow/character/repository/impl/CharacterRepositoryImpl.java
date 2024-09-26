package wow.character.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.CharacterTemplateId;
import wow.character.model.character.CombatRatingInfo;
import wow.character.repository.CharacterRepository;
import wow.character.repository.impl.parser.character.CharacterExcelParser;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.spell.TalentRepository;
import wow.commons.util.GameVersionMap;
import wow.commons.util.PhaseMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Repository
@RequiredArgsConstructor
public class CharacterRepositoryImpl implements CharacterRepository {
	private final Map<GameVersionId, GameVersion> gameVersionById = new TreeMap<>();
	private final Map<PhaseId, Phase> phaseById = new TreeMap<>();
	private final PhaseMap<String, CharacterTemplate> characterTemplateByKey = new PhaseMap<>();
	private final GameVersionMap<Integer, CombatRatingInfo> combatRatingInfoByLevel = new GameVersionMap<>();
	private final GameVersionMap<String, BaseStatInfo> baseStatInfoByKey = new GameVersionMap<>();

	private final TalentRepository talentRepository;

	@Value("${character.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<GameVersion> getGameVersion(GameVersionId gameVersionId) {
		return Optional.ofNullable(gameVersionById.get(gameVersionId));
	}

	@Override
	public Optional<Phase> getPhase(PhaseId phaseId) {
		return Optional.ofNullable(phaseById.get(phaseId));
	}

	@Override
	public Optional<BaseStatInfo> getBaseStatInfo(GameVersionId gameVersionId, CharacterClassId characterClassId, RaceId raceId, int level) {
		var key = getBaseStatInfoKey(characterClassId, raceId, level);
		return baseStatInfoByKey.getOptional(gameVersionId, key);
	}

	@Override
	public Optional<CombatRatingInfo> getCombatRatingInfo(GameVersionId gameVersionId, int level) {
		return combatRatingInfoByLevel.getOptional(gameVersionId, level);
	}

	@Override
	public Optional<CharacterTemplate> getCharacterTemplate(CharacterTemplateId characterTemplateId, CharacterClassId characterClassId, int level, PhaseId phaseId) {
		var key = getCharacterTemplateKey(characterTemplateId, characterClassId, level);
		return characterTemplateByKey.getOptional(phaseId, key);
	}

	@Override
	public Optional<CharacterTemplate> getDefaultCharacterTemplate(CharacterClassId characterClassId, int level, PhaseId phaseId) {
		return characterTemplateByKey.values(phaseId).stream()
				.filter(x -> x.getCharacterClassId() == characterClassId)
				.filter(x -> x.getLevel() == level)
				.filter(CharacterTemplate::isDefault)
				.findFirst();
	}

	@PostConstruct
	public void init() throws IOException {
		var excelParser = new CharacterExcelParser(xlsFilePath, this, talentRepository);
		excelParser.readFromXls();
	}

	public void addGameVersion(GameVersion gameVersion) {
		gameVersionById.put(gameVersion.getGameVersionId(), gameVersion);
	}

	public void addPhase(Phase phase) {
		phaseById.put(phase.getPhaseId(), phase);
	}

	public void addCharacterTemplate(CharacterTemplate characterTemplate) {
		String key = getCharacterTemplateKey(characterTemplate.getCharacterTemplateId(), characterTemplate.getCharacterClassId(), characterTemplate.getLevel());
		putForEveryPhase(characterTemplateByKey, key, characterTemplate);
	}

	private static String getCharacterTemplateKey(CharacterTemplateId characterTemplateId, CharacterClassId characterClassId, int level) {
		return characterTemplateId + "#" + characterClassId + "#" + level;
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

	public void addCombatRatingInfo(CombatRatingInfo combatRatingInfo) {
		combatRatingInfoByLevel.put(combatRatingInfo.getGameVersion().getGameVersionId(), combatRatingInfo.getLevel(), combatRatingInfo);
	}
}

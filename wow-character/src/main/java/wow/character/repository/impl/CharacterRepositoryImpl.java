package wow.character.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.CharacterTemplateId;
import wow.character.model.character.GameVersion;
import wow.character.model.character.Phase;
import wow.character.repository.CharacterRepository;
import wow.character.repository.impl.parser.character.CharacterExcelParser;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.SpellRepository;
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

	private final SpellRepository spellRepository;

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
	public Optional<CharacterTemplate> getCharacterTemplate(CharacterTemplateId characterTemplateId, CharacterClassId characterClassId, int level, PhaseId phaseId) {
		String key = getCharacterTemplateKey(characterTemplateId, characterClassId, level);
		return characterTemplateByKey.getOptional(phaseId, key);
	}

	@PostConstruct
	public void init() throws IOException {
		var excelParser = new CharacterExcelParser(xlsFilePath, this, spellRepository);
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
}

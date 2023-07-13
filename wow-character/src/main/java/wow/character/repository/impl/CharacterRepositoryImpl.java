package wow.character.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.CharacterTemplateId;
import wow.character.model.character.GameVersion;
import wow.character.model.character.Phase;
import wow.character.repository.CharacterRepository;
import wow.character.repository.impl.parsers.character.CharacterExcelParser;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.ExcelRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Repository
@RequiredArgsConstructor
public class CharacterRepositoryImpl extends ExcelRepository implements CharacterRepository {
	private final Map<GameVersionId, GameVersion> gameVersionById = new TreeMap<>();
	private final Map<PhaseId, Phase> phaseById = new TreeMap<>();
	private final Map<String, List<CharacterTemplate>> characterTemplateByKey = new HashMap<>();

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
		return getUnique(characterTemplateByKey, key, phaseId);
	}

	@PostConstruct
	public void init() throws IOException {
		var excelParser = new CharacterExcelParser(xlsFilePath, this);
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
		addEntry(characterTemplateByKey, key, characterTemplate);
	}

	private static String getCharacterTemplateKey(CharacterTemplateId characterTemplateId, CharacterClassId characterClassId, int level) {
		return characterTemplateId + "#" + characterClassId + "#" + level;
	}
}

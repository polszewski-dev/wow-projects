package wow.character.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.CharacterTemplateId;
import wow.character.repository.CharacterTemplateRepository;
import wow.character.repository.impl.parser.character.CharacterTemplateExcelParser;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.pve.PhaseRepository;
import wow.commons.repository.spell.TalentRepository;
import wow.commons.util.PhaseMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Repository
@RequiredArgsConstructor
public class CharacterTemplateRepositoryImpl implements CharacterTemplateRepository {
	private final PhaseMap<String, CharacterTemplate> characterTemplateByKey = new PhaseMap<>();

	private final TalentRepository talentRepository;
	private final PhaseRepository phaseRepository;

	@Value("${character.templates.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<CharacterTemplate> getCharacterTemplate(CharacterTemplateId characterTemplateId, CharacterClassId characterClassId, int level, PhaseId phaseId) {
		String key = getCharacterTemplateKey(characterTemplateId, characterClassId, level);
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
		var excelParser = new CharacterTemplateExcelParser(xlsFilePath, this, talentRepository, phaseRepository);
		excelParser.readFromXls();
	}

	public void addCharacterTemplate(CharacterTemplate characterTemplate) {
		String key = getCharacterTemplateKey(characterTemplate.getCharacterTemplateId(), characterTemplate.getCharacterClassId(), characterTemplate.getLevel());
		putForEveryPhase(characterTemplateByKey, key, characterTemplate);
	}

	private static String getCharacterTemplateKey(CharacterTemplateId characterTemplateId, CharacterClassId characterClassId, int level) {
		return characterTemplateId + "#" + characterClassId + "#" + level;
	}
}

package wow.character.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.PlayerCharacter;
import wow.character.repository.CharacterTemplateRepository;
import wow.character.repository.impl.parser.character.CharacterTemplateExcelParser;
import wow.commons.model.character.CharacterClassId;
import wow.commons.repository.pve.PhaseRepository;
import wow.commons.repository.spell.TalentRepository;
import wow.commons.util.PhaseMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static wow.commons.util.PhaseMap.addEntryForEveryPhase;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
@RequiredArgsConstructor
public class CharacterTemplateRepositoryImpl implements CharacterTemplateRepository {
	private record Key(CharacterClassId characterClassId, int level) {}

	private final PhaseMap<Key, List<CharacterTemplate>> characterTemplateByKey = new PhaseMap<>();

	private final TalentRepository talentRepository;
	private final PhaseRepository phaseRepository;

	@Value("${character.templates.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<CharacterTemplate> getCharacterTemplate(String name, PlayerCharacter character) {
		return getCharacterTemplateStream(character)
				.filter(x -> x.getName().equals(name))
				.findAny();
	}

	@Override
	public Optional<CharacterTemplate> getDefaultCharacterTemplate(PlayerCharacter character) {
		return getCharacterTemplateStream(character)
				.filter(CharacterTemplate::isDefault)
				.findAny();
	}

	private Stream<CharacterTemplate> getCharacterTemplateStream(PlayerCharacter character) {
		var key = new Key(character.getCharacterClassId(), character.getLevel());

		return characterTemplateByKey.getOptional(character.getPhaseId(), key).stream()
				.flatMap(Collection::stream)
				.filter(x -> x.isAvailableTo(character));
	}

	@PostConstruct
	public void init() throws IOException {
		var excelParser = new CharacterTemplateExcelParser(xlsFilePath, this, talentRepository, phaseRepository);
		excelParser.readFromXls();
	}

	public void addCharacterTemplate(CharacterTemplate characterTemplate) {
		var characterClassId = characterTemplate.getRequiredCharacterClassId();
		int maxLevel = characterTemplate.getRequiredMaxLevel();

		for (int level = characterTemplate.getRequiredLevel(); level <= maxLevel; ++level) {
			var key = new Key(characterClassId, level);

			addEntryForEveryPhase(characterTemplateByKey, key, characterTemplate);
		}
	}
}

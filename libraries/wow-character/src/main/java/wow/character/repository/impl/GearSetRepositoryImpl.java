package wow.character.repository.impl;

import org.springframework.stereotype.Component;
import wow.character.model.character.Character;
import wow.character.model.character.GearSet;
import wow.character.repository.GearSetRepository;
import wow.character.repository.impl.parser.character.GearSetExcelParser;
import wow.commons.model.character.CharacterClassId;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.PhaseMap.addEntryForEveryPhase;

/**
 * User: POlszewski
 * Date: 2025-03-26
 */
@Component
public class GearSetRepositoryImpl implements GearSetRepository {
	private record NameClassKey(String name, CharacterClassId characterClassId) {}

	private final PhaseMap<NameClassKey, List<GearSet>> gearSetByNameClass = new PhaseMap<>();
	private final PhaseMap<CharacterClassId, List<GearSet>> gearSetByClass = new PhaseMap<>();

	public GearSetRepositoryImpl(GearSetExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getGearSets().forEach(this::addGearSet);
	}

	@Override
	public Optional<GearSet> getGearSet(String name, Character character) {
		var phaseId = character.getPhaseId();
		var key = new NameClassKey(name, character.getCharacterClassId());

		return gearSetByNameClass.getOptional(phaseId, key).stream()
				.flatMap(Collection::stream)
				.filter(x -> x.isAvailableTo(character))
				.findAny();
	}

	@Override
	public List<GearSet> getAvailableGearSets(Character character) {
		var phaseId = character.getPhaseId();
		var characterClassId = character.getCharacterClassId();

		return gearSetByClass.getOptional(phaseId, characterClassId).stream()
				.flatMap(Collection::stream)
				.filter(x -> x.isAvailableTo(character))
				.toList();
	}

	private void addGearSet(GearSet gearSet) {
		var name = gearSet.getName();
		var characterClassId = gearSet.getRequiredCharacterClassId();
		var key = new NameClassKey(name, characterClassId);

		addEntryForEveryPhase(gearSetByNameClass, key, gearSet);
		addEntryForEveryPhase(gearSetByClass, characterClassId, gearSet);
	}
}

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
	private record Key(String name, CharacterClassId characterClassId) {}

	private final PhaseMap<Key, List<GearSet>> gearSetByKey = new PhaseMap<>();

	public GearSetRepositoryImpl(GearSetExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getGearSets().forEach(this::addGearSet);
	}

	@Override
	public Optional<GearSet> getGearSet(String name, Character character) {
		var key = new Key(name, character.getCharacterClassId());

		return gearSetByKey.getOptional(character.getPhaseId(), key).stream()
				.flatMap(Collection::stream)
				.filter(x -> x.isAvailableTo(character))
				.findAny();
	}

	private void addGearSet(GearSet gearSet) {
		var key = new Key(gearSet.getName(), gearSet.getRequiredCharacterClassId());

		addEntryForEveryPhase(gearSetByKey, key, gearSet);
	}
}

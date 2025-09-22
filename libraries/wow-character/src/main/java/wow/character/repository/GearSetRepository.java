package wow.character.repository;

import wow.character.model.character.Character;
import wow.character.model.character.GearSet;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2025-03-26
 */
public interface GearSetRepository {
	Optional<GearSet> getGearSet(String name, Character character);

	List<GearSet> getAvailableGearSets(Character character);
}

package wow.simulator.model.unit;

import wow.character.model.character.PetCharacter;
import wow.commons.model.AnyDuration;

/**
 * User: POlszewski
 * Date: 09.08.2026
 */
public interface Pet extends Unit, PetCharacter {
	@Override
	Unit getMaster();

	AnyDuration getRemainingDuration();

	String UNSUMMON_PET = "Unsummon Pet";
}

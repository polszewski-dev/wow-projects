package wow.character.model.character;

import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
public interface NonPlayerCharacter extends Character {
	@Override
	default boolean hasProfession(ProfessionId professionId) {
		return false;
	}

	@Override
	default boolean hasProfession(ProfessionId professionId, int level) {
		return false;
	}

	@Override
	default boolean hasProfessionSpecialization(ProfessionSpecializationId specializationId) {
		return false;
	}

	@Override
	default boolean hasExclusiveFaction(String exclusiveFaction) {
		return false;
	}

	@Override
	default NonPlayerCharacter getMaster() {
		return this;
	}
}

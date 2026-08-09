package wow.character.model.character;

import wow.commons.model.character.PetType;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;

/**
 * User: POlszewski
 * Date: 09.08.2026
 */
public interface PetCharacter extends Character, CharacterRestricted {
	Character getMaster();

	void setMaster(Character master);

	PetType getPetType();

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
}

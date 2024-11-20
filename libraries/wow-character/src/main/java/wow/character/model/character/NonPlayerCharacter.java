package wow.character.model.character;

import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.Side;
import wow.commons.model.talent.TalentId;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
public interface NonPlayerCharacter extends Character {
	@Override
	default PetType getActivePetType() {
		return null;
	}

	@Override
	default RaceId getRaceId() {
		return null;
	}

	@Override
	default Side getSide() {
		return null;
	}

	@Override
	default PveRole getRole() {
		return null;
	}

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
	default boolean hasActivePet(PetType petType) {
		return false;
	}

	@Override
	default boolean hasExclusiveFaction(ExclusiveFaction exclusiveFaction) {
		return false;
	}

	@Override
	default boolean hasTalent(TalentId talentId) {
		return false;
	}
}

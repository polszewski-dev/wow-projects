package wow.character.model.character;

import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Spell;

/**
 * User: POlszewski
 * Date: 09.08.2026
 */
public interface PetCharacter extends Character, CharacterRestricted {
	Character getMaster();

	void setMaster(Character master);

	Spell getSourceSpell();

	@Override
	default CharacterRestriction getCharacterRestriction() {
		return getSourceSpell() instanceof Ability ability
				? ability.getCharacterRestriction()
				: CharacterRestriction.EMPTY;
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
	default boolean hasExclusiveFaction(String exclusiveFaction) {
		return false;
	}
}

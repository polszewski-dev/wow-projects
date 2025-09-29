package wow.character.model.character;

import wow.character.model.effect.EffectCollector;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.Side;

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
	default boolean hasExclusiveFaction(String exclusiveFaction) {
		return false;
	}

	@Override
	default boolean hasTalent(String name) {
		return false;
	}

	@Override
	default boolean hasTalent(String name, int rank) {
		return false;
	}

	@Override
	default void collectEffects(EffectCollector collector) {
		getBuffs().collectEffects(collector);
	}
}

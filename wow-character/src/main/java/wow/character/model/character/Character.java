package wow.character.model.character;

import wow.character.model.effect.EffectCollection;
import wow.commons.model.buff.BuffIdAndRank;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.PetType;
import wow.commons.model.config.CharacterInfo;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ResourceType;

import java.util.Collection;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public interface Character extends CharacterInfo, EffectCollection {
	Phase getPhase();

	default PhaseId getPhaseId() {
		return getPhase().getPhaseId();
	}

	default GameVersion getGameVersion() {
		return getPhase().getGameVersion();
	}

	default GameVersionId getGameVersionId() {
		return getPhase().getGameVersionId();
	}

	CharacterClass getCharacterClass();

	@Override
	default CharacterClassId getCharacterClassId() {
		return getCharacterClass().getCharacterClassId();
	}

	CreatureType getCreatureType();

	PetType getActivePetType();

	Character getTarget();

	void setTarget(Character target);

	BaseStatInfo getBaseStatInfo();

	CombatRatingInfo getCombatRatingInfo();

	default double getBaseStatValue(ResourceType resourceType) {
		return switch (resourceType) {
			case HEALTH -> getBaseStatInfo().getBaseHealth();
			case MANA -> getBaseStatInfo().getBaseMana();
			default -> throw new IllegalArgumentException("Unhandled resource: " + resourceType);
		};
	}

	Spellbook getSpellbook();

	default Optional<Ability> getAbility(AbilityId abilityId) {
		return getSpellbook().getAbility(abilityId);
	}

	default Optional<Ability> getAbility(AbilityId abilityId, int rank) {
		return getSpellbook().getAbility(abilityId, rank);
	}

	@Override
	default boolean hasAbility(AbilityId abilityId) {
		return getAbility(abilityId).isPresent();
	}

	Buffs getBuffs();

	default void setBuffs(Collection<BuffIdAndRank> buffIds) {
		getBuffs().set(buffIds);
	}

	default void resetBuffs() {
		getBuffs().reset();
	}

	default Buffs getBuffList(BuffListType buffListType) {
		return switch (buffListType) {
			case CHARACTER_BUFF -> getBuffs();
			case TARGET_DEBUFF -> getTarget().getBuffs();
		};
	}

	static int getLevelDifference(Character caster, Character target) {
		return target.getLevel() - caster.getLevel();
	}
}

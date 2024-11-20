package wow.minmax.model.impl;

import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Buffs;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.Spellbook;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.minmax.model.Character;
import wow.minmax.model.NonPlayerCharacter;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
public class NonPlayerCharacterImpl extends wow.character.model.character.impl.NonPlayerCharacterImpl implements NonPlayerCharacter {
	public NonPlayerCharacterImpl(
			Phase phase,
			CharacterClass characterClass,
			CreatureType creatureType,
			int level,
			CombatRatingInfo combatRatingInfo
	) {
		super(phase, characterClass, creatureType, level, combatRatingInfo);
	}

	private NonPlayerCharacterImpl(
			Phase phase,
			CharacterClass characterClass,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Spellbook spellbook,
			Buffs buffs,
			CreatureType creatureType
	) {
		super(phase, characterClass, level, baseStatInfo, combatRatingInfo, spellbook, buffs, creatureType);
	}

	@Override
	public Character getTarget() {
		return (Character) super.getTarget();
	}

	@Override
	public void setTarget(wow.character.model.character.Character target) {
		if (target != null && !(target instanceof Character)) {
			throw new IllegalArgumentException();
		}
		super.setTarget(target);
	}

	@Override
	public NonPlayerCharacter copy() {
		var copy = new NonPlayerCharacterImpl(
				getPhase(),
				getCharacterClass(),
				getLevel(),
				getBaseStatInfo(),
				getCombatRatingInfo(),
				getSpellbook().copy(),
				getBuffs().copy(),
				getCreatureType()
		);
		copy.setTarget(getTarget());
		return copy;
	}
}

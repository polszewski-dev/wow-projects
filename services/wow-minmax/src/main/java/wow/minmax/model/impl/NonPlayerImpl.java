package wow.minmax.model.impl;

import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.minmax.model.NonPlayer;
import wow.minmax.model.Unit;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
public class NonPlayerImpl extends NonPlayerCharacterImpl implements NonPlayer {
	public NonPlayerImpl(
			Phase phase,
			CharacterClass characterClass,
			CreatureType creatureType,
			int level,
			CombatRatingInfo combatRatingInfo
	) {
		super(phase, characterClass, creatureType, level, combatRatingInfo);
	}

	private NonPlayerImpl(
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
	public Unit getTarget() {
		return (Unit) super.getTarget();
	}

	@Override
	public void setTarget(Character target) {
		if (target != null && !(target instanceof Unit)) {
			throw new IllegalArgumentException();
		}
		super.setTarget(target);
	}

	@Override
	public NonPlayer copy() {
		var copy = new NonPlayerImpl(
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

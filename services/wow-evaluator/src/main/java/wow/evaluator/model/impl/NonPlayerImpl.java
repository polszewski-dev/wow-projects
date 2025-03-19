package wow.evaluator.model.impl;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.character.impl.CharacterImpl;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.evaluator.model.NonPlayer;
import wow.evaluator.model.Unit;

import static wow.character.model.character.BaseStatInfo.getDummyBaseStatInfo;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@Getter
public class NonPlayerImpl extends CharacterImpl implements NonPlayer {
	private final CreatureType creatureType;

	public NonPlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			CreatureType creatureType,
			int level,
			CombatRatingInfo combatRatingInfo
	) {
		super(name, phase, characterClass, level, getDummyBaseStatInfo(characterClass, level, phase), combatRatingInfo);
		this.creatureType = creatureType;
	}

	private NonPlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Spellbook spellbook,
			Buffs buffs,
			CreatureType creatureType
	) {
		super(name, phase, characterClass, level, baseStatInfo, combatRatingInfo, spellbook, buffs);
		this.creatureType = creatureType;
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
				getName(),
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

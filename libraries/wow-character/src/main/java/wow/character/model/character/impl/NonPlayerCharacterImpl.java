package wow.character.model.character.impl;

import lombok.Getter;
import wow.character.model.character.*;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
@Getter
public class NonPlayerCharacterImpl extends CharacterImpl implements NonPlayerCharacter {
	private final CreatureType creatureType;

	public NonPlayerCharacterImpl(Phase phase, CharacterClass characterClass, CreatureType creatureType, int level, CombatRatingInfo combatRatingInfo) {
		super(phase, characterClass, level, getDummyBaseStatInfo(characterClass, level, phase), combatRatingInfo);
		this.creatureType = creatureType;
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
		super(phase, characterClass, level, baseStatInfo, combatRatingInfo, spellbook, buffs);
		this.creatureType = creatureType;
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		getBuffs().collectEffects(collector);
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

	private static BaseStatInfo getDummyBaseStatInfo(CharacterClass characterClass, int level, Phase phase) {
		return new BaseStatInfo(level, characterClass, null, 0, 0, 0, 0, 0, 1, 0, Percent.ZERO, 1, phase.getGameVersion());
	}
}

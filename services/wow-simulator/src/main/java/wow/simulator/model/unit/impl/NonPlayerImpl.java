package wow.simulator.model.unit.impl;

import lombok.Getter;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.simulator.model.unit.NonPlayer;

import static wow.character.model.character.BaseStatInfo.getDummyBaseStatInfo;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class NonPlayerImpl extends UnitImpl implements NonPlayer {
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
		this.resources.setHealth(1_000_000_000, 1_000_000_000);
	}

	@Override
	public void collectAuras(EffectCollector collector) {
		// void
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		NonPlayer.super.collectEffects(collector);
		effects.collectEffects(collector);
	}
}

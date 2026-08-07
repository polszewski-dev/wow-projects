package wow.estimator.model.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.character.impl.CharacterImpl;
import wow.character.model.effect.EffectCollector;
import wow.character.model.equipment.Equipment;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.estimator.model.EffectInstances;
import wow.estimator.model.NonPlayer;
import wow.estimator.model.Unit;

import static wow.character.model.character.BaseStatInfo.getDummyBaseStatInfo;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@Getter
public class NonPlayerImpl extends CharacterImpl implements NonPlayer {
	private final EffectInstances effectInstances;
	@Setter
	private Percent healthPct = Percent._100;

	public NonPlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			CreatureType creatureType,
			int level,
			CombatRatingInfo combatRatingInfo
	) {
		super(name, phase, characterClass, level, creatureType, getDummyBaseStatInfo(characterClass, level, phase), combatRatingInfo);
		this.effectInstances = new EffectInstances();
	}

	private NonPlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			CreatureType creatureType,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Spellbook spellbook,
			Equipment equipment,
			Consumables consumables,
			EffectInstances effectInstances
	) {
		super(name, phase, characterClass, level, creatureType, baseStatInfo, combatRatingInfo, spellbook, equipment, consumables);
		this.effectInstances = effectInstances;
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
				getCreatureType(),
				getBaseStatInfo(),
				getCombatRatingInfo(),
				getSpellbook().copy(),
				getEquipment().copy(),
				getConsumables().copy(),
				getEffectInstances().copy()
		);
		copy.setTarget(getTarget());
		return copy;
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		NonPlayer.super.collectEffects(collector);
		getEffectInstances().collectEffects(collector);
	}
}

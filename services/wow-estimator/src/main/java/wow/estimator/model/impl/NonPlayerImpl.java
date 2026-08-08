package wow.estimator.model.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.character.impl.CharacterImpl;
import wow.character.model.effect.EffectCollector;
import wow.character.model.equipment.Equipment;
import wow.character.model.talent.Talents;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.estimator.model.EffectInstances;
import wow.estimator.model.NonPlayer;
import wow.estimator.model.Unit;

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
			int level,
			CreatureType creatureType,
			Side side,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents
	) {
		super(name, phase, characterClass, level, creatureType, side, baseStatInfo, combatRatingInfo, talents);
		this.effectInstances = new EffectInstances();
	}

	private NonPlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			CreatureType creatureType,
			Side side,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents,
			Spellbook spellbook,
			Equipment equipment,
			Consumables consumables,
			EffectInstances effectInstances
	) {
		super(name, phase, characterClass, level, creatureType, side, baseStatInfo, combatRatingInfo, talents, spellbook, equipment, consumables);
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
				getSide(),
				getBaseStatInfo(),
				getCombatRatingInfo(),
				getTalents().copy(),
				getSpellbook().copy(),
				getEquipment().copy(),
				getConsumables().copy(),
				getEffectInstances().copy()
		);
		copy.setTarget(getTarget());
		copy.setRole(getRole());
		copy.setScript(getScript());
		return copy;
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		NonPlayer.super.collectEffects(collector);
		getEffectInstances().collectEffects(collector);
	}
}

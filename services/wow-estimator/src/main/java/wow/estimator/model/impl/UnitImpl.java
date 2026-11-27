package wow.estimator.model.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.Spellbook;
import wow.character.model.character.impl.CharacterImpl;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.talent.Talent;
import wow.estimator.model.EffectInstance;
import wow.estimator.model.EffectInstances;
import wow.estimator.model.Unit;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@Getter
public abstract class UnitImpl extends CharacterImpl implements Unit {
	private final EffectInstances effectInstances;

	@Setter
	private Percent healthPct = Percent._100;

	protected UnitImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			CreatureType creatureType,
			Race race,
			Side side,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			List<Talent> availableTalents
	) {
		super(name, phase, characterClass, level, creatureType, race, side, baseStatInfo, combatRatingInfo, availableTalents);
		this.effectInstances = new EffectInstances(this);
	}

	protected UnitImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			CreatureType creatureType,
			Race race,
			Side side,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			List<Talent> availableTalents,
			Spellbook spellbook
	) {
		super(name, phase, characterClass, level, creatureType, race, side, baseStatInfo, combatRatingInfo, availableTalents, spellbook);
		this.effectInstances = new EffectInstances(this);
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
	public void setFocus(Character focus) {
		if (focus != null && !(focus instanceof Unit)) {
			throw new IllegalArgumentException();
		}
		super.setFocus(focus);
	}

	@Override
	public void effectApplied(EffectInstance effectInstance) {
		//todo
	}

	@Override
	public void effectRemoved(EffectInstance effectInstance) {
		//todo
	}
}

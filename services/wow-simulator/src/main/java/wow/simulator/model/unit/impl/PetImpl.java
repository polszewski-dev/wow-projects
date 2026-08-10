package wow.simulator.model.unit.impl;

import lombok.Getter;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.effect.EffectCollector;
import wow.character.model.talent.Talents;
import wow.commons.model.Duration;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.PetType;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.Spell;
import wow.simulator.model.unit.Pet;
import wow.simulator.model.unit.Unit;

import java.util.List;

/**
 * User: POlszewski
 * Date: 09.08.2026
 */
@Getter
public class PetImpl extends UnitImpl implements Pet {
	private final PetType petType;
	private final Spell sourceSpell;
	private Unit master;

	public PetImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			PetType petType,
			Race race,
			Side side,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents,
			Spell sourceSpell

	) {
		super(name, phase, characterClass, level, petType.getCreatureType(), race, side, baseStatInfo, combatRatingInfo, talents);
		this.petType = petType;
		this.sourceSpell = sourceSpell;
	}

	public void setMaster(Character master) {
		this.master = (Unit) master;
	}

	@Override
	public void collectAuras(EffectCollector collector) {
		// void
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		effects.collectEffects(collector);
	}

	@Override
	public void regen(Duration sinceLastRegen) {
		// void
	}

	@Override
	public List<Unit> getPartyMembers() {
		return master.getPartyMembers();
	}

	@Override
	public void onAddedToSimulation() {
		getResources().setHealthToMax();
		getResources().setManaToMax();
	}
}

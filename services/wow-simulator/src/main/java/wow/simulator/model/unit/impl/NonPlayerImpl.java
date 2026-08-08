package wow.simulator.model.unit.impl;

import lombok.Getter;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.Party;
import wow.character.model.character.Raid;
import wow.character.model.effect.EffectCollector;
import wow.character.model.talent.Talents;
import wow.commons.model.Duration;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.simulator.model.unit.NonPlayer;

import static wow.character.model.character.BaseStatInfo.getDummyBaseStatInfo;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class NonPlayerImpl extends UnitImpl implements NonPlayer, Party.OnAdd<NonPlayer> {
	private Party<NonPlayer> party;

	public NonPlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			CreatureType creatureType,
			Side side,
			CombatRatingInfo combatRatingInfo,
			Talents talents
	) {
		super(name, phase, characterClass, level, creatureType, side, getDummyBaseStatInfo(characterClass, level, phase), combatRatingInfo, talents);

		Raid.newRaid(this);
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

	@Override
	public void regen(Duration sinceLastRegen) {
		// void
	}

	@Override
	public void onAddedToSimulation() {
		addHiddenEffect("Bonus Stamina", 100_000_000);
		getResources().setHealthToMax();
		getResources().setManaToMax();
	}

	@Override
	public void onAdd(Party<NonPlayer> party) {
		if (this.party != null) {
			this.party.remove(this);
		}
		this.party = party;
	}
}

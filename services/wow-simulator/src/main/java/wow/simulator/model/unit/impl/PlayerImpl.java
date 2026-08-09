package wow.simulator.model.unit.impl;

import lombok.Getter;
import wow.character.model.character.*;
import wow.character.model.effect.EffectCollector;
import wow.character.model.talent.Talents;
import wow.character.util.AbstractEffectCollector;
import wow.commons.model.Duration;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.effect.Effect;
import wow.commons.model.pve.Phase;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Cost;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;

import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class PlayerImpl extends UnitImpl implements Player, Party.OnAdd<Player> {
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Assets assets;

	private Time lastTimeManaSpent;

	@Getter
	private Party<Player> party;

	public PlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			Race race,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents,
			CharacterProfessions professions,
			ExclusiveFactions exclusiveFactions
	) {
		super(name, phase, characterClass, level, race.getCreatureType(), race, race.getSide(), baseStatInfo, combatRatingInfo, talents);
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.assets = new Assets();

		Raid.newRaid(this);
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		super.collectEffects(collector);
		effects.collectEffects(collector);
		collectAurasFromOtherPartyMembers(collector);
	}

	private void collectAurasFromOtherPartyMembers(EffectCollector collector) {
		var auraCollector = new AuraCollector(this, collector);

		getParty().forEachMemberOrPet(player -> {
			if (player != this) {
				player.collectAuras(auraCollector);
			}
		});
	}

	@Override
	public void collectAuras(EffectCollector collector) {
		getEquipment().collectEffects(collector);

		for (var racial : getRacials()) {
			collector.addEffect(racial);
		}

		effects.collectEffects(collector);
	}

	private static class AuraCollector extends AbstractEffectCollector.OnlyEffects {
		private final EffectCollector collector;

		public AuraCollector(Unit unit, EffectCollector collector) {
			super(unit);
			this.collector = collector;
		}

		@Override
		public void addEffect(Effect effect, int stackCount) {
			if (effect.hasAugmentedAbilities() || !effect.isAura()) {
				return;
			}

			collector.addEffect(effect, stackCount);
		}
	}

	@Override
	protected void paySpellCost(Ability ability, Cost cost) {
		super.paySpellCost(ability, cost);

		if (cost.resourceType() == MANA && cost.amount() > 0) {
			this.lastTimeManaSpent = now();
		}
	}

	@Override
	public void regen(Duration sinceLastRegen) {
		var snapshot = getCharacterCalculationService().getRegenSnapshot(this);
		var sinceLastManaSpent = getSinceLastManaSpent();
		var health = snapshot.getHealthToRegen(true, sinceLastRegen);
		var mana = snapshot.getManaToRegen(sinceLastManaSpent, sinceLastRegen);

		increaseHealth(health, false, null, null);
		increaseMana(mana, false, null, null);
	}

	private Duration getSinceLastManaSpent() {
		return lastTimeManaSpent != null ? now().subtract(lastTimeManaSpent) : null;
	}

	@Override
	public void onAddedToSimulation() {
		getResources().setHealthToMax();
		getResources().setManaToMax();
	}

	@Override
	public void onAdd(Party<Player> party) {
		if (this.party != null) {
			this.party.remove(this);
		}
		this.party = party;
	}
}

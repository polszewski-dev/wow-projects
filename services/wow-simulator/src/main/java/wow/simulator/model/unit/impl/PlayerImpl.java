package wow.simulator.model.unit.impl;

import lombok.Getter;
import wow.character.model.build.Build;
import wow.character.model.build.Talents;
import wow.character.model.character.*;
import wow.character.model.effect.EffectCollector;
import wow.character.model.equipment.Equipment;
import wow.character.util.AbstractEffectCollector;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.effect.Effect;
import wow.commons.model.pve.Phase;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.ability.ShootAbility;

import java.util.Optional;

import static wow.commons.model.categorization.ItemSlot.RANGED;
import static wow.commons.model.spell.AbilityId.SHOOT;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class PlayerImpl extends UnitImpl implements Player {
	private final Race race;
	private final Build build;
	private final Equipment equipment;
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Consumables consumables;

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
		super(name, phase, characterClass, level, baseStatInfo, combatRatingInfo);
		this.race = race;
		this.build = new Build(phase.getGameVersion(), talents);
		this.equipment = new Equipment();
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.consumables = new Consumables();
		this.resources.setHealth(1_000_000_000, 1_000_000_000);
	}

	@Override
	public Optional<Ability> getAbility(AbilityId abilityId) {
		return Player.super.getAbility(abilityId)
				.map(this::replaceShoot);
	}

	private Ability replaceShoot(Ability ability) {
		if (!ability.getAbilityId().equals(SHOOT)) {
			return ability;
		}

		var rangedWeapon = getEquippedItem(RANGED);

		if (rangedWeapon == null || rangedWeapon.getWeaponStats() == null) {
			return ability;
		}

		return new ShootAbility(ability, rangedWeapon);
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		getBuild().collectEffects(collector);
		getEquipment().collectEffects(collector);
		getBuffs().collectEffects(collector);
		for (var racial : getRace().getRacials(this)) {
			collector.addEffect(racial);
		}
		effects.collectEffects(collector);
		collectAurasFromOtherPartyMembers(collector);
	}

	private void collectAurasFromOtherPartyMembers(EffectCollector collector) {
		var auraCollector = new AuraCollector(this, collector);

		getParty().forEachPartyMember(player -> {
			if (player != this) {
				player.collectAuras(auraCollector);
			}
		});
	}

	@Override
	public void collectAuras(EffectCollector collector) {
		getEquipment().collectEffects(collector);

		for (var racial : getRace().getRacials(this)) {
			collector.addEffect(racial);
		}
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
}

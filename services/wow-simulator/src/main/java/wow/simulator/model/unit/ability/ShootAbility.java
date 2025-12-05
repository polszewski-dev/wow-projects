package wow.simulator.model.unit.ability;

import wow.character.model.equipment.EquippableItem;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.DirectComponent;

import java.util.List;
import java.util.Objects;

import static wow.commons.model.spell.component.ComponentCommand.DealDamageDirectly;

/**
 * User: POlszewski
 * Date: 2025-02-09
 */
public class ShootAbility implements Ability {
	private final Ability ability;
	private final EquippableItem rangedWeapon;

	public ShootAbility(Ability ability, EquippableItem rangedWeapon) {
		this.ability = ability;
		this.rangedWeapon = rangedWeapon;
		Objects.requireNonNull(ability);
		Objects.requireNonNull(rangedWeapon);
	}

	@Override
	public DirectComponent getDirectComponent() {
		var weaponStats = rangedWeapon.getWeaponStats();

		var command = new DealDamageDirectly(
				SpellTargets.ENEMY,
				SpellTargetCondition.EMPTY,
				new Coefficient(Percent.ZERO, weaponStats.damageType()),
				weaponStats.damageMin(),
				weaponStats.damageMax(),
				null
		);

		return new DirectComponent(List.of(command));
	}

	@Override
	public CastInfo getCastInfo() {
		return new CastInfo(rangedWeapon.getWeaponStats().speed(), false, false);
	}

	@Override
	public AbilityId getAbilityId() {
		return ability.getAbilityId();
	}

	@Override
	public AbilityCategory getCategory() {
		return ability.getCategory();
	}

	@Override
	public Cost getCost() {
		return ability.getCost();
	}

	@Override
	public int getRange() {
		return ability.getRange();
	}

	@Override
	public AbilityId getEffectRemovedOnHit() {
		return ability.getEffectRemovedOnHit();
	}

	@Override
	public int getRank() {
		return ability.getRank();
	}

	@Override
	public AbilityNameRank getNameRank() {
		return ability.getNameRank();
	}

	@Override
	public SpellId getId() {
		return ability.getId();
	}

	@Override
	public SpellType getType() {
		return ability.getType();
	}

	@Override
	public SpellSchool getSchool() {
		return ability.getSchool();
	}

	@Override
	public Duration getCooldown() {
		return ability.getCooldown();
	}

	@Override
	public boolean isBolt() {
		return true;
	}

	@Override
	public EffectApplication getEffectApplication() {
		return ability.getEffectApplication();
	}

	@Override
	public boolean hasDamagingComponent() {
		return ability.hasDamagingComponent();
	}

	@Override
	public boolean hasHealingComponent() {
		return ability.hasHealingComponent();
	}

	@Override
	public boolean hasDamagingPeriodicComponent() {
		return ability.hasDamagingPeriodicComponent();
	}

	@Override
	public CharacterRestriction getCharacterRestriction() {
		return ability.getCharacterRestriction();
	}

	@Override
	public Description getDescription() {
		return ability.getDescription();
	}

	@Override
	public TimeRestriction getTimeRestriction() {
		return ability.getTimeRestriction();
	}

	@Override
	public String toString() {
		return ability.toString();
	}
}

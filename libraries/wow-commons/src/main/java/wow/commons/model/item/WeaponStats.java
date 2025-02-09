package wow.commons.model.item;

import wow.commons.model.Duration;
import wow.commons.model.spell.SpellSchool;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public record WeaponStats(
		int damageMin,
		int damageMax,
		SpellSchool damageType,
		double dps,
		Duration speed
) {
	public WeaponStats withDamageMin(int weaponDamageMin) {
		return new WeaponStats(weaponDamageMin, damageMax, damageType, dps, speed);
	}

	public WeaponStats withDamageMax(int weaponDamageMax) {
		return new WeaponStats(damageMin, weaponDamageMax, damageType, dps, speed);
	}

	public WeaponStats withDamageType(SpellSchool damageType) {
		return new WeaponStats(damageMin, damageMax, damageType, dps, speed);
	}

	public WeaponStats withWeaponDps(double dps) {
		return new WeaponStats(damageMin, damageMax, damageType, dps, speed);
	}

	public WeaponStats withWeaponSpeed(Duration speed) {
		return new WeaponStats(damageMin, damageMax, damageType, dps, speed);
	}

	@Override
	public String toString() {
		return String.format(
				"%s - %s%s, dps: %s, speed: %s",
				damageMin, damageMax, (damageType != null ? " " + damageType : ""),
				dps,
				speed
		);
	}
}

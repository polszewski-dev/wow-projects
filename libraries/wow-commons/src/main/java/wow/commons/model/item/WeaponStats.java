package wow.commons.model.item;

import wow.commons.model.spell.SpellSchool;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public record WeaponStats(
		int weaponDamageMin,
		int weaponDamageMax,
		SpellSchool damageType,
		double weaponDps,
		double weaponSpeed
) {
	public WeaponStats weaponDamageMin(int weaponDamageMin) {
		return new WeaponStats(weaponDamageMin, weaponDamageMax, damageType, weaponDps, weaponSpeed);
	}

	public WeaponStats weaponDamageMax(int weaponDamageMax) {
		return new WeaponStats(weaponDamageMin, weaponDamageMax, damageType, weaponDps, weaponSpeed);
	}

	public WeaponStats damageType(SpellSchool damageType) {
		return new WeaponStats(weaponDamageMin, weaponDamageMax, damageType, weaponDps, weaponSpeed);
	}

	public WeaponStats weaponDps(double weaponDps) {
		return new WeaponStats(weaponDamageMin, weaponDamageMax, damageType, weaponDps, weaponSpeed);
	}

	public WeaponStats weaponSpeed(double weaponSpeed) {
		return new WeaponStats(weaponDamageMin, weaponDamageMax, damageType, weaponDps, weaponSpeed);
	}

	@Override
	public String toString() {
		return String.format(
				"%s - %s%s, dps: %s, speed: %s",
				weaponDamageMin, weaponDamageMax, (damageType != null ? " " + damageType : ""),
				weaponDps,
				weaponSpeed
		);
	}
}

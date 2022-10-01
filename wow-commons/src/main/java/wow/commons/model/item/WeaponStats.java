package wow.commons.model.item;

import wow.commons.model.spells.SpellSchool;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public class WeaponStats {
	private int weaponDamageMin;
	private int weaponDamageMax;
	private SpellSchool damageType;
	private double weaponDps;
	private double weaponSpeed;

	public int getWeaponDamageMin() {
		return weaponDamageMin;
	}

	public void setWeaponDamageMin(int weaponDamageMin) {
		this.weaponDamageMin = weaponDamageMin;
	}

	public int getWeaponDamageMax() {
		return weaponDamageMax;
	}

	public void setWeaponDamageMax(int weaponDamageMax) {
		this.weaponDamageMax = weaponDamageMax;
	}

	public SpellSchool getDamageType() {
		return damageType;
	}

	public void setDamageType(SpellSchool damageType) {
		this.damageType = damageType;
	}

	public double getWeaponDps() {
		return weaponDps;
	}

	public void setWeaponDps(double weaponDps) {
		this.weaponDps = weaponDps;
	}

	public double getWeaponSpeed() {
		return weaponSpeed;
	}

	public void setWeaponSpeed(double weaponSpeed) {
		this.weaponSpeed = weaponSpeed;
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

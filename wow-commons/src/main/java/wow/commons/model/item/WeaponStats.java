package wow.commons.model.item;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spells.SpellSchool;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
@Getter
@Setter
public class WeaponStats {
	private int weaponDamageMin;
	private int weaponDamageMax;
	private SpellSchool damageType;
	private double weaponDps;
	private double weaponSpeed;

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

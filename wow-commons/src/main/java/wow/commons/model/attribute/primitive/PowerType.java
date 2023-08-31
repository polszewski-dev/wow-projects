package wow.commons.model.attribute.primitive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-03-15
 */
@AllArgsConstructor
@Getter
public enum PowerType {
	WEAPON("Weapon"),
	MELEE("Melee"),
	RANGED("Ranged"),
	SPELL_DAMAGE("SpellDamage"),
	HEALING("Healing");

	private final String key;

	public static PowerType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static PowerType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}

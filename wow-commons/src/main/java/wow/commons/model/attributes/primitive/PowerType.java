package wow.commons.model.attributes.primitive;

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
	ANY("", true, true, true, true, true),
	WEAPON("Weapon", false, false, false, false, false),
	PHYSICAL("Physical", false, true, true, false, false),
	MELEE("Melee", false, true, false, false, false),
	RANGED("Ranged", false, false, true, false, false),
	SPELL("Spell", false, false, false, true, true),
	SPELL_DAMAGE("SpellDamage", false, false, false, true, false),
	HEALING("Healing", false, false, false, false, true);

	private final String key;
	private final boolean weapon;
	private final boolean melee;
	private final boolean ranged;
	private final boolean spellDamage;
	private final boolean healing;

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

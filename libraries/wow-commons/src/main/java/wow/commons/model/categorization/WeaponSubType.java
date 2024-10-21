package wow.commons.model.categorization;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@AllArgsConstructor
public enum WeaponSubType implements ItemSubType {
	DAGGER("Dagger"),
	SWORD("Sword"),
	AXE("Axe"),
	POLEARM("Polearm"),
	MACE("Mace"),
	STAFF("Staff"),
	FIST_WEAPON("Fist Weapon"),

	HELD_IN_OFF_HAND("Held In Off-hand"),
	SHIELD("Shield"),

	WAND("Wand"),
	GUN("Gun"),
	BOW("Bow"),
	CROSSBOW("Crossbow"),
	THROWN("Thrown"),

	TOTEM("Totem"),
	LIBRAM("Libram"),
	IDOL("Idol");

	private final String key;

	public static WeaponSubType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}

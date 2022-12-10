package wow.commons.model.character;

import lombok.AllArgsConstructor;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.WeaponSubType;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@AllArgsConstructor
public enum WeaponProfficiency {
	DAGGER(WeaponSubType.DAGGER),
	MAIN_HAND_DAGGER(ItemType.MAIN_HAND, WeaponSubType.DAGGER),
	ONE_HAND_DAGGER(ItemType.ONE_HAND, WeaponSubType.DAGGER),

	SWORD(WeaponSubType.SWORD),
	MAIN_HAND_SWORD(ItemType.MAIN_HAND, WeaponSubType.SWORD),
	OFF_HAND_SWORD(ItemType.OFF_HAND, WeaponSubType.SWORD),
	ONE_HAND_SWORD(ItemType.ONE_HAND, WeaponSubType.SWORD),
	TWO_HAND_SWORD(ItemType.TWO_HAND, WeaponSubType.SWORD),

	AXE(WeaponSubType.AXE),
	MAIN_HAND_AXE(ItemType.MAIN_HAND, WeaponSubType.AXE),
	OFF_HAND_AXE(ItemType.OFF_HAND, WeaponSubType.AXE),
	ONE_HAND_AXE(ItemType.ONE_HAND, WeaponSubType.AXE),
	TWO_HAND_AXE(ItemType.TWO_HAND, WeaponSubType.AXE),

	POLEARM(WeaponSubType.POLEARM),

	MACE(WeaponSubType.MACE),
	MAIN_HAND_MACE(ItemType.MAIN_HAND, WeaponSubType.MACE),
	OFF_HAND_MACE(ItemType.OFF_HAND, WeaponSubType.MACE),
	ONE_HAND_MACE(ItemType.ONE_HAND, WeaponSubType.MACE),
	TWO_HAND_MACE(ItemType.TWO_HAND, WeaponSubType.MACE),

	STAFF(WeaponSubType.STAFF),

	FIST_WEAPON(WeaponSubType.FIST_WEAPON),

	HELD_IN_OFF_HAND(WeaponSubType.HELD_IN_OFF_HAND),
	SHIELD(WeaponSubType.SHIELD),

	WAND(WeaponSubType.WAND),

	GUN(WeaponSubType.GUN),
	BOW(WeaponSubType.BOW),
	CROSSBOW(WeaponSubType.CROSSBOW),
	THROWN(WeaponSubType.THROWN),

	IDOL(WeaponSubType.IDOL),
	TOTEM(WeaponSubType.TOTEM),
	LIBRAM(WeaponSubType.LIBRAM);

	private final ItemType weaponType;
	private final WeaponSubType weaponSubType;

	WeaponProfficiency(WeaponSubType weaponSubType) {
		this(null, weaponSubType);
	}

	public boolean matches(ItemType weaponType, WeaponSubType weaponSubType) {
		return (this.weaponType == null || this.weaponType == weaponType) && this.weaponSubType == weaponSubType;
	}
}

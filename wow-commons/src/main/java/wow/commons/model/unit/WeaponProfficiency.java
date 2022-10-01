package wow.commons.model.unit;

import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.WeaponSubType;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public enum WeaponProfficiency {
	Dagger(WeaponSubType.Dagger),
	MainHandDagger(ItemType.MainHand, WeaponSubType.Dagger),
	OneHandDagger(ItemType.OneHand, WeaponSubType.Dagger),

	Sword(WeaponSubType.Sword),
	MainHandSword(ItemType.MainHand, WeaponSubType.Sword),
	OffHandSword(ItemType.OffHand, WeaponSubType.Sword),
	OneHandSword(ItemType.OneHand, WeaponSubType.Sword),
	TwoHandSword(ItemType.TwoHand, WeaponSubType.Sword),

	Axe(WeaponSubType.Axe),
	MainHandAxe(ItemType.MainHand, WeaponSubType.Axe),
	OffHandAxe(ItemType.OffHand, WeaponSubType.Axe),
	OneHandAxe(ItemType.OneHand, WeaponSubType.Axe),
	TwoHandAxe(ItemType.TwoHand, WeaponSubType.Axe),

	Polearm(WeaponSubType.Polearm),

	Mace(WeaponSubType.Mace),
	MainHandMace(ItemType.MainHand, WeaponSubType.Mace),
	OffHandMace(ItemType.OffHand, WeaponSubType.Mace),
	OneHandMace(ItemType.OneHand, WeaponSubType.Mace),
	TwoHandMace(ItemType.TwoHand, WeaponSubType.Mace),

	Staff(WeaponSubType.Staff),

	FistWeapon(WeaponSubType.FistWeapon),

	HeldInOffHand(WeaponSubType.HeldInOffHand),
	Shield(WeaponSubType.Shield),

	Wand(WeaponSubType.Wand),

	Gun(WeaponSubType.Gun),
	Bow(WeaponSubType.Bow),
	Crossbow(WeaponSubType.Crossbow),
	Thrown(WeaponSubType.Thrown),

	Idol(WeaponSubType.Idol),
	Totem(WeaponSubType.Totem),
	Libram(WeaponSubType.Libram),
	;

	private final ItemType weaponType;
	private final WeaponSubType weaponSubType;

	WeaponProfficiency(WeaponSubType weaponSubType) {
		this(null, weaponSubType);
	}

	WeaponProfficiency(ItemType weaponType, WeaponSubType weaponSubType) {
		if (weaponSubType == null) {
			throw new NullPointerException();
		}
		this.weaponType = weaponType;
		this.weaponSubType = weaponSubType;
	}

	public boolean matches(ItemType weaponType, WeaponSubType weaponSubType) {
		return (this.weaponType == null || this.weaponType == weaponType) && this.weaponSubType == weaponSubType;
	}

	public static boolean matches(CharacterClass characterClass, ItemType weaponType, WeaponSubType weaponSubType) {
		return characterClass.getWeaponProfficiencies().stream().anyMatch(x -> x.matches(weaponType, weaponSubType));
	}
}

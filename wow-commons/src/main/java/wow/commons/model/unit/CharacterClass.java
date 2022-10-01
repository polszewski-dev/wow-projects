package wow.commons.model.unit;

import java.util.List;

import static wow.commons.model.unit.ArmorProfficiency.*;
import static wow.commons.model.unit.WeaponProfficiency.*;

/**
 * User: POlszewski
 * Date: 2019-11-02
 */
public enum CharacterClass {
	Mage(List.of(Cloth), List.of(
			MainHandDagger, OneHandDagger,
			MainHandSword, OneHandSword,
			Staff,
			HeldInOffHand,
			Wand
	)),
	Warlock(List.of(Cloth), List.of(
			MainHandDagger, OneHandDagger,
			MainHandSword, OneHandSword,
			Staff,
			HeldInOffHand,
			Wand
	)),
	Priest(List.of(Cloth), List.of(
			MainHandDagger, OneHandDagger,
			MainHandMace, OneHandMace,
			Staff,
			HeldInOffHand,
			Wand
	)),
	Druid(List.of(Cloth, Leather), List.of(
			MainHandDagger, OneHandDagger,
			MainHandMace, OneHandMace, TwoHandMace,
			Staff,
			FistWeapon,
			HeldInOffHand,
			Idol
	)),
	Rogue(List.of(Cloth, Leather), List.of(
			Dagger,
			MainHandSword, OffHandSword, OneHandSword,
			MainHandAxe, OffHandAxe, OneHandAxe,
			MainHandMace, OffHandMace, OneHandMace,
			FistWeapon,
			Gun, Bow, Crossbow
	)),
	Hunter(List.of(Cloth, Leather, Mail), List.of(
			Dagger,
			Sword,
			Axe,
			Polearm,
			Mace,
			Staff,
			FistWeapon,
			Gun, Bow, Crossbow
	)),
	Shaman(List.of(Cloth, Leather, Mail), List.of(
			Dagger,
			Mace,
			Staff,
			FistWeapon,
			HeldInOffHand, Shield,
			Totem
	)),
	Paladin(List.of(Cloth, Leather, Mail, Plate), List.of(
			MainHandDagger, OneHandDagger,
			MainHandSword, OneHandSword, TwoHandSword,
			MainHandAxe, OneHandAxe, TwoHandAxe,
			Polearm,
			MainHandMace, OneHandMace, TwoHandMace,
			Staff,
			HeldInOffHand, Shield,
			Libram
	)),
	Warrior(List.of(Cloth, Leather, Mail, Plate), List.of(
			Dagger,
			Sword,
			Axe,
			Polearm,
			Mace,
			Staff,
			FistWeapon,
			Shield,
			Gun, Bow, Crossbow
	)),
	;

	private final List<ArmorProfficiency> armorProfficiencies;
	private final List<WeaponProfficiency> weaponProfficiencies;

	CharacterClass(List<ArmorProfficiency> armorProfficiencies, List<WeaponProfficiency> weaponProfficiencies) {
		this.armorProfficiencies = armorProfficiencies;
		this.weaponProfficiencies = weaponProfficiencies;
	}

	public static CharacterClass parse(String value) {
		return CharacterClass.valueOf(value);
	}

	public List<ArmorProfficiency> getArmorProfficiencies() {
		return armorProfficiencies;
	}

	public List<WeaponProfficiency> getWeaponProfficiencies() {
		return weaponProfficiencies;
	}
}

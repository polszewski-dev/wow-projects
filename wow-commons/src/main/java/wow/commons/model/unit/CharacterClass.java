package wow.commons.model.unit;

import wow.commons.util.EnumUtil;

import java.util.List;

import static wow.commons.model.unit.ArmorProfficiency.*;
import static wow.commons.model.unit.WeaponProfficiency.*;

/**
 * User: POlszewski
 * Date: 2019-11-02
 */
public enum CharacterClass {
	MAGE(List.of(CLOTH), List.of(
			MAIN_HAND_DAGGER, ONE_HAND_DAGGER,
			MAIN_HAND_SWORD, ONE_HAND_SWORD,
			STAFF,
			HELD_IN_OFF_HAND,
			WAND
	)),
	WARLOCK(List.of(CLOTH), List.of(
			MAIN_HAND_DAGGER, ONE_HAND_DAGGER,
			MAIN_HAND_SWORD, ONE_HAND_SWORD,
			STAFF,
			HELD_IN_OFF_HAND,
			WAND
	)),
	PRIEST(List.of(CLOTH), List.of(
			MAIN_HAND_DAGGER, ONE_HAND_DAGGER,
			MAIN_HAND_MACE, ONE_HAND_MACE,
			STAFF,
			HELD_IN_OFF_HAND,
			WAND
	)),
	DRUID(List.of(CLOTH, LEATHER), List.of(
			MAIN_HAND_DAGGER, ONE_HAND_DAGGER,
			MAIN_HAND_MACE, ONE_HAND_MACE, TWO_HAND_MACE,
			STAFF,
			FIST_WEAPON,
			HELD_IN_OFF_HAND,
			IDOL
	)),
	ROGUE(List.of(CLOTH, LEATHER), List.of(
			DAGGER,
			MAIN_HAND_SWORD, OFF_HAND_SWORD, ONE_HAND_SWORD,
			MAIN_HAND_AXE, OFF_HAND_AXE, ONE_HAND_AXE,
			MAIN_HAND_MACE, OFF_HAND_MACE, ONE_HAND_MACE,
			FIST_WEAPON,
			GUN, BOW, CROSSBOW
	)),
	HUNTER(List.of(CLOTH, LEATHER, MAIL), List.of(
			DAGGER,
			SWORD,
			AXE,
			POLEARM,
			MACE,
			STAFF,
			FIST_WEAPON,
			GUN, BOW, CROSSBOW
	)),
	SHAMAN(List.of(CLOTH, LEATHER, MAIL), List.of(
			DAGGER,
			MACE,
			STAFF,
			FIST_WEAPON,
			HELD_IN_OFF_HAND, SHIELD,
			TOTEM
	)),
	PALADIN(List.of(CLOTH, LEATHER, MAIL, PLATE), List.of(
			MAIN_HAND_DAGGER, ONE_HAND_DAGGER,
			MAIN_HAND_SWORD, ONE_HAND_SWORD, TWO_HAND_SWORD,
			MAIN_HAND_AXE, ONE_HAND_AXE, TWO_HAND_AXE,
			POLEARM,
			MAIN_HAND_MACE, ONE_HAND_MACE, TWO_HAND_MACE,
			STAFF,
			HELD_IN_OFF_HAND, SHIELD,
			LIBRAM
	)),
	WARRIOR(List.of(CLOTH, LEATHER, MAIL, PLATE), List.of(
			DAGGER,
			SWORD,
			AXE,
			POLEARM,
			MACE,
			STAFF,
			FIST_WEAPON,
			SHIELD,
			GUN, BOW, CROSSBOW
	));

	private final List<ArmorProfficiency> armorProfficiencies;
	private final List<WeaponProfficiency> weaponProfficiencies;

	CharacterClass(List<ArmorProfficiency> armorProfficiencies, List<WeaponProfficiency> weaponProfficiencies) {
		this.armorProfficiencies = armorProfficiencies;
		this.weaponProfficiencies = weaponProfficiencies;
	}

	public static CharacterClass parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public List<ArmorProfficiency> getArmorProfficiencies() {
		return armorProfficiencies;
	}

	public List<WeaponProfficiency> getWeaponProfficiencies() {
		return weaponProfficiencies;
	}
}

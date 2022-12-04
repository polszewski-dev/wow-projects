package wow.commons.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.categorization.*;
import wow.commons.util.EnumUtil;

import java.util.List;

import static wow.commons.model.character.ArmorProfficiency.matches;
import static wow.commons.model.character.ArmorProfficiency.*;
import static wow.commons.model.character.WeaponProfficiency.matches;
import static wow.commons.model.character.WeaponProfficiency.*;

/**
 * User: POlszewski
 * Date: 2019-11-02
 */
@AllArgsConstructor
@Getter
public enum CharacterClass {
	MAGE(List.of(CLOTH), List.of(
			MAIN_HAND_DAGGER, ONE_HAND_DAGGER,
			MAIN_HAND_SWORD, ONE_HAND_SWORD,
			STAFF,
			HELD_IN_OFF_HAND,
			WAND
	), false),
	WARLOCK(List.of(CLOTH), List.of(
			MAIN_HAND_DAGGER, ONE_HAND_DAGGER,
			MAIN_HAND_SWORD, ONE_HAND_SWORD,
			STAFF,
			HELD_IN_OFF_HAND,
			WAND
	), false),
	PRIEST(List.of(CLOTH), List.of(
			MAIN_HAND_DAGGER, ONE_HAND_DAGGER,
			MAIN_HAND_MACE, ONE_HAND_MACE,
			STAFF,
			HELD_IN_OFF_HAND,
			WAND
	), false),
	DRUID(List.of(CLOTH, LEATHER), List.of(
			MAIN_HAND_DAGGER, ONE_HAND_DAGGER,
			MAIN_HAND_MACE, ONE_HAND_MACE, TWO_HAND_MACE,
			STAFF,
			FIST_WEAPON,
			HELD_IN_OFF_HAND,
			IDOL
	), false),
	ROGUE(List.of(CLOTH, LEATHER), List.of(
			DAGGER,
			MAIN_HAND_SWORD, OFF_HAND_SWORD, ONE_HAND_SWORD,
			MAIN_HAND_AXE, OFF_HAND_AXE, ONE_HAND_AXE,
			MAIN_HAND_MACE, OFF_HAND_MACE, ONE_HAND_MACE,
			FIST_WEAPON,
			GUN, BOW, CROSSBOW
	), true),
	HUNTER(List.of(CLOTH, LEATHER, MAIL), List.of(
			DAGGER,
			SWORD,
			AXE,
			POLEARM,
			MACE,
			STAFF,
			FIST_WEAPON,
			GUN, BOW, CROSSBOW
	), true),
	SHAMAN(List.of(CLOTH, LEATHER, MAIL), List.of(
			DAGGER,
			MACE,
			STAFF,
			FIST_WEAPON,
			HELD_IN_OFF_HAND, SHIELD,
			TOTEM
	), true),
	PALADIN(List.of(CLOTH, LEATHER, MAIL, PLATE), List.of(
			MAIN_HAND_DAGGER, ONE_HAND_DAGGER,
			MAIN_HAND_SWORD, ONE_HAND_SWORD, TWO_HAND_SWORD,
			MAIN_HAND_AXE, ONE_HAND_AXE, TWO_HAND_AXE,
			POLEARM,
			MAIN_HAND_MACE, ONE_HAND_MACE, TWO_HAND_MACE,
			STAFF,
			HELD_IN_OFF_HAND, SHIELD,
			LIBRAM
	), false),
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
	), true);

	private final List<ArmorProfficiency> armorProfficiencies;
	private final List<WeaponProfficiency> weaponProfficiencies;
	private final boolean dualWield;

	public static CharacterClass parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean canEquip(ItemSlot itemSlot, ItemType itemType, ItemSubType itemSubType) {
		switch (itemType.getCategory()) {
			case WEAPON:
				if (itemSlot == ItemSlot.OFF_HAND && !isWeaponAllowedInOffHand(itemType)) {
					return false;
				}
				return matches(this, itemType, (WeaponSubType) itemSubType);
			case ARMOR:
				return matches(this, (ArmorSubType) itemSubType);
			case ACCESSORY:
				return true;
			default:
				return false;
		}
	}

	private boolean isWeaponAllowedInOffHand(ItemType itemType) {
		return dualWield || itemType == ItemType.OFF_HAND;
	}
}

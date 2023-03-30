package wow.commons.model.character;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import wow.commons.model.categorization.ItemCategory;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemType.ONE_HAND;
import static wow.commons.model.categorization.WeaponSubType.SWORD;

/**
 * User: POlszewski
 * Date: 2022-12-08
 */
class CharacterClassTest {
	@ParameterizedTest(name = "[{index}] Can equip: slot = {0}, type = {1}, subType = {2}")
	@CsvSource({
			"HEAD, HEAD, CLOTH",
			"NECK, NECK, ",
			"SHOULDER, SHOULDER, CLOTH",
			"BACK, BACK, CLOTH",
			"CHEST, CHEST, CLOTH",
			"WRIST, WRIST, CLOTH",
			"HANDS, HANDS, CLOTH",
			"WAIST, WAIST, CLOTH",
			"LEGS, LEGS, CLOTH",
			"FEET, FEET, CLOTH",
			"FINGER_1, FINGER, ",
			"FINGER_2, FINGER, ",
			"TRINKET_1, TRINKET, ",
			"TRINKET_2, TRINKET, ",
			"MAIN_HAND, TWO_HAND, STAFF",
			"MAIN_HAND, ONE_HAND, SWORD",
			"MAIN_HAND, ONE_HAND, DAGGER",
			"MAIN_HAND, MAIN_HAND, SWORD",
			"MAIN_HAND, MAIN_HAND, DAGGER",
			"OFF_HAND, OFF_HAND, HELD IN OFF-HAND",
			"RANGED, RANGED, WAND",
	})
	void canEquip(ItemSlot itemSlot, ItemType itemType, String itemSubType) {
		CharacterClassId warlock = CharacterClassId.WARLOCK;

		assertThat(warlock.canEquip(itemSlot, itemType, ItemSubType.parse(itemSubType))).isTrue();
	}

	@ParameterizedTest(name = "[{index}] Can not equip: slot = {0}, type = {1}, subType = {2}")
	@CsvSource({
			"HEAD, HEAD, LEATHER",
			"HEAD, HEAD, MAIL",
			"HEAD, HEAD, PLATE",
			"SHOULDER, SHOULDER, LEATHER",
			"CHEST, CHEST, LEATHER",
			"WRIST, WRIST, LEATHER",
			"HANDS, HANDS, LEATHER",
			"WAIST, WAIST, LEATHER",
			"LEGS, LEGS, LEATHER",
			"FEET, FEET, LEATHER",
			"MAIN_HAND, TWO_HAND, AXE",
			"MAIN_HAND, TWO_HAND, SWORD",
			"MAIN_HAND, TWO_HAND, POLEARM",
			"MAIN_HAND, TWO_HAND, MACE",
			"MAIN_HAND, ONE_HAND, AXE",
			"MAIN_HAND, ONE_HAND, MACE",
			"MAIN_HAND, ONE_HAND, FIST WEAPON",
			"MAIN_HAND, MAIN_HAND, AXE",
			"MAIN_HAND, MAIN_HAND, MACE",
			"MAIN_HAND, MAIN_HAND, FIST WEAPON",
			"OFF_HAND, ONE_HAND, AXE",
			"OFF_HAND, ONE_HAND, MACE",
			"OFF_HAND, ONE_HAND, FIST WEAPON",
			"OFF_HAND, ONE_HAND, FIST WEAPON",
			"OFF_HAND, ONE_HAND, DAGGER",
			"OFF_HAND, ONE_HAND, SWORD",
			"OFF_HAND, OFF_HAND, SHIELD",
			"RANGED, RANGED, THROWN",
			"RANGED, RANGED, BOW",
			"RANGED, RANGED, CROSSBOW",
			"RANGED, RANGED, TOTEM",
			"RANGED, RANGED, LIBRAM",
			"RANGED, RANGED, IDOL",
			"RANGED, RANGED, IDOL",
			"RANGED, RANGED, IDOL",
	})
	void canNotEquip(ItemSlot itemSlot, ItemType itemType, String itemSubType) {
		CharacterClassId warlock = CharacterClassId.WARLOCK;

		assertThat(warlock.isDualWield()).isFalse();
		assertThat(warlock.canEquip(itemSlot, itemType, ItemSubType.parse(itemSubType))).isFalse();
	}

	@Test
	void testDualWield() {
		CharacterClassId warrior = CharacterClassId.WARRIOR;

		assertThat(warrior.isDualWield()).isTrue();
		assertThat(warrior.canEquip(ItemSlot.MAIN_HAND, ONE_HAND, SWORD)).isTrue();
		assertThat(warrior.canEquip(ItemSlot.OFF_HAND, ONE_HAND, SWORD)).isTrue();
	}

	@ParameterizedTest
	@EnumSource(CharacterClassId.class)
	void nobodyCanEquipThese(CharacterClassId characterClassId) {
		for (ItemType itemType : ItemType.values()) {
			ItemCategory category = itemType.getCategory();
			if (category != ItemCategory.ARMOR && category != ItemCategory.WEAPON && category != ItemCategory.ACCESSORY) {
				assertThat(characterClassId.canEquip(ItemSlot.CHEST, itemType, null)).isFalse();
			}
		}
	}
}
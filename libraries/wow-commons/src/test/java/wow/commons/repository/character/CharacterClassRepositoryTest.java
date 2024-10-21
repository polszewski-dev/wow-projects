package wow.commons.repository.character;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.categorization.ItemCategory;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.Race;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemType.ONE_HAND;
import static wow.commons.model.categorization.WeaponSubType.SWORD;
import static wow.commons.model.character.ArmorProfficiency.CLOTH;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CharacterClassId.WARRIOR;
import static wow.commons.model.character.RaceId.*;
import static wow.commons.model.character.WeaponProfficiency.*;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.GameVersionId.VANILLA;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
class CharacterClassRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	CharacterClassRepository underTest;

	@Test
	void classesAreCorrect() {
		var warlock = underTest.getCharacterClass(WARLOCK, VANILLA).orElseThrow();

		assertThat(warlock.getCharacterClassId()).isEqualTo(WARLOCK);
		assertThat(warlock.getName()).isEqualTo("Warlock");
		assertThat(warlock.getIcon()).isEqualTo("classicon_warlock");
		assertThat(warlock.getArmorProfficiencies()).hasSameElementsAs(List.of(CLOTH));

		assertThat(warlock.getWeaponProfficiencies()).hasSameElementsAs(List.of(
				MAIN_HAND_DAGGER, ONE_HAND_DAGGER, MAIN_HAND_SWORD, ONE_HAND_SWORD, STAFF, HELD_IN_OFF_HAND, WAND
		));

		assertThat(warlock.isDualWield()).isFalse();
		assertThat(warlock.getGameVersion().getGameVersionId()).isEqualTo(VANILLA);

		assertThat(warlock.getRaces().stream().map(Race::getRaceId).toList()).hasSameElementsAs(List.of(
				UNDEAD, ORC, HUMAN, GNOME
		));
	}

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
		var warlock = underTest.getCharacterClass(WARLOCK, TBC).orElseThrow();

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
		var warlock = underTest.getCharacterClass(WARLOCK, TBC).orElseThrow();

		assertThat(warlock.isDualWield()).isFalse();
		assertThat(warlock.canEquip(itemSlot, itemType, ItemSubType.parse(itemSubType))).isFalse();
	}

	@Test
	void testDualWield() {
		var warrior = underTest.getCharacterClass(WARRIOR, TBC).orElseThrow();

		assertThat(warrior.isDualWield()).isTrue();
		assertThat(warrior.canEquip(ItemSlot.MAIN_HAND, ONE_HAND, SWORD)).isTrue();
		assertThat(warrior.canEquip(ItemSlot.OFF_HAND, ONE_HAND, SWORD)).isTrue();
	}

	@ParameterizedTest
	@EnumSource(CharacterClassId.class)
	void nobodyCanEquipThese(CharacterClassId characterClassId) {
		for (var itemType : ItemType.values()) {
			var category = itemType.getCategory();

			if (category != ItemCategory.ARMOR && category != ItemCategory.WEAPON && category != ItemCategory.ACCESSORY) {
				var classInfo = underTest.getCharacterClass(characterClassId, TBC).orElseThrow();

				assertThat(classInfo.canEquip(ItemSlot.CHEST, itemType, null)).isFalse();
			}
		}
	}
}
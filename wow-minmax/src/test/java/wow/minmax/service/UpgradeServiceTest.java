package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.equipment.ItemFilter;
import wow.character.model.equipment.ItemSocket;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class UpgradeServiceTest extends ServiceTest {
	@Autowired
	UpgradeService underTest;

	@ParameterizedTest
	@EnumSource(value = ItemSlot.class, names = {
			"HEAD",
			"NECK",
			"SHOULDER",
			"BACK",
			"CHEST",
			"WRIST",
			"HANDS",
			"WAIST",
			"LEGS",
			"FEET",
			"RANGED"
	})
	void findUpgradesChestNoEquippedItems(ItemSlot itemSlot) {
		character.getEquipment().reset();

		var slotGroup = ItemSlotGroup.getGroup(itemSlot).orElseThrow();
		var upgrades = underTest.findUpgrades(character, slotGroup, ItemFilter.everything());

		assertThat(upgrades).hasSize(10);
	}

	@ParameterizedTest
	@EnumSource(value = ItemSlot.class, names = {
			"HEAD",
			"NECK",
			"SHOULDER",
			"BACK",
			"CHEST",
			"WRIST",
			"HANDS",
			"WAIST",
			"LEGS",
			"FEET",
			"RANGED"
	})
	void findUpgradesChest(ItemSlot itemSlot) {
		character.equip(null, itemSlot);

		var slotGroup = ItemSlotGroup.getGroup(itemSlot).orElseThrow();
		var upgrades = underTest.findUpgrades(character, slotGroup, ItemFilter.everything());

		assertThat(upgrades).hasSize(10);
	}

	@Test
	void findUpgradesRings() {
		character.equip(null, ItemSlot.FINGER_1);
		character.equip(null, ItemSlot.FINGER_2);

		var upgrades = underTest.findUpgrades(character, ItemSlotGroup.FINGERS, ItemFilter.everything());

		assertThat(upgrades).hasSize(10);
	}

	@Test
	void findUpgradesTrinkets() {
		character.equip(null, ItemSlot.TRINKET_1);
		character.equip(null, ItemSlot.TRINKET_2);

		var upgrades = underTest.findUpgrades(character, ItemSlotGroup.TRINKETS, ItemFilter.everything());

		assertThat(upgrades).hasSize(10);
	}

	@Test
	void findUpgradesWeapons() {
		character.equip(null, ItemSlot.MAIN_HAND);
		character.equip(null, ItemSlot.OFF_HAND);

		var upgrades = underTest.findUpgrades(character, ItemSlotGroup.WEAPONS, ItemFilter.everything());

		assertThat(upgrades).hasSize(10);
	}

	@Test
	void getBestItemVariant() {
		var item = underTest.getBestItemVariant(character, getItem("Sunfire Robe").getItem(), ItemSlot.CHEST);

		assertThat(item.getEnchant()).isNotNull();

		for (ItemSocket socket : item.getSockets()) {
			assertThat(socket.getGem()).isNotNull();
		}
	}

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		character.setEquipment(getEquipment());
	}
}
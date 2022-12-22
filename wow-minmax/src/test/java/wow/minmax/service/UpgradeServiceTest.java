package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.ItemSocket;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.model.Comparison;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class UpgradeServiceTest extends ServiceTest {
	@Autowired
	UpgradeService underTest;

	@Test
	void findUpgradesChest() {
		character.equip(null, ItemSlot.CHEST);

		List<Comparison> upgrades = underTest.findUpgrades(character, ItemSlotGroup.CHEST, character.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesRings() {
		character.equip(null, ItemSlot.FINGER_1);
		character.equip(null, ItemSlot.FINGER_2);

		List<Comparison> upgrades = underTest.findUpgrades(character, ItemSlotGroup.FINGERS, character.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesTrinkets() {
		character.equip(null, ItemSlot.TRINKET_1);
		character.equip(null, ItemSlot.TRINKET_2);

		List<Comparison> upgrades = underTest.findUpgrades(character, ItemSlotGroup.TRINKETS, character.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesWeapons() {
		character.equip(null, ItemSlot.MAIN_HAND);
		character.equip(null, ItemSlot.OFF_HAND);

		List<Comparison> upgrades = underTest.findUpgrades(character, ItemSlotGroup.WEAPONS, character.getDamagingSpell());

		assertThat(upgrades).hasSize(3);
	}

	@Test
	void getBestItemVariant() {
		EquippableItem item = underTest.getBestItemVariant(character, getItem("Sunfire Robe").getItem(), ItemSlot.CHEST, character.getDamagingSpell());

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
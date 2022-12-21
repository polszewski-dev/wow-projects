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
		profile.equip(null, ItemSlot.CHEST);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.CHEST, profile.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesRings() {
		profile.equip(null, ItemSlot.FINGER_1);
		profile.equip(null, ItemSlot.FINGER_2);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.FINGERS, profile.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesTrinkets() {
		profile.equip(null, ItemSlot.TRINKET_1);
		profile.equip(null, ItemSlot.TRINKET_2);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.TRINKETS, profile.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesWeapons() {
		profile.equip(null, ItemSlot.MAIN_HAND);
		profile.equip(null, ItemSlot.OFF_HAND);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.WEAPONS, profile.getDamagingSpell());

		assertThat(upgrades).hasSize(3);
	}

	@Test
	void getBestItemVariant() {
		EquippableItem item = underTest.getBestItemVariant(profile, getItem("Sunfire Robe").getItem(), ItemSlot.CHEST, profile.getDamagingSpell());

		assertThat(item.getEnchant()).isNotNull();

		for (ItemSocket socket : item.getSockets()) {
			assertThat(socket.getGem()).isNotNull();
		}
	}

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		profile.setEquipment(getEquipment());
	}
}
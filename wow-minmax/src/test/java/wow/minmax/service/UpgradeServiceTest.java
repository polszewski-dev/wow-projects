package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.ItemSocket;
import wow.minmax.model.Comparison;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.BuffSetId.*;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class UpgradeServiceTest extends ServiceTest {
	@Autowired
	UpgradeService underTest;

	@Test
	void findUpgradesChest() {
		playerProfile.setEquipment(getEquipment());
		playerProfile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		playerProfile.getEquipment().set(null, ItemSlot.CHEST);

		List<Comparison> upgrades = underTest.findUpgrades(playerProfile, ItemSlotGroup.CHEST, playerProfile.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesRings() {
		playerProfile.setEquipment(getEquipment());
		playerProfile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		playerProfile.getEquipment().set(null, ItemSlot.FINGER_1);
		playerProfile.getEquipment().set(null, ItemSlot.FINGER_2);

		List<Comparison> upgrades = underTest.findUpgrades(playerProfile, ItemSlotGroup.FINGERS, playerProfile.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesTrinkets() {
		playerProfile.setEquipment(getEquipment());
		playerProfile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		playerProfile.getEquipment().set(null, ItemSlot.TRINKET_1);
		playerProfile.getEquipment().set(null, ItemSlot.TRINKET_2);

		List<Comparison> upgrades = underTest.findUpgrades(playerProfile, ItemSlotGroup.TRINKETS, playerProfile.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesWeapons() {
		playerProfile.setEquipment(getEquipment());
		playerProfile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		playerProfile.getEquipment().set(null, ItemSlot.MAIN_HAND);
		playerProfile.getEquipment().set(null, ItemSlot.OFF_HAND);

		List<Comparison> upgrades = underTest.findUpgrades(playerProfile, ItemSlotGroup.WEAPONS, playerProfile.getDamagingSpell());

		assertThat(upgrades).hasSize(3);
	}

	@Test
	void getBestItemVariant() {
		playerProfile.setEquipment(getEquipment());
		playerProfile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		EquippableItem item = underTest.getBestItemVariant(playerProfile, getItem("Sunfire Robe").getItem(), ItemSlot.CHEST, playerProfile.getDamagingSpell());

		assertThat(item.getEnchant()).isNotNull();

		for (ItemSocket socket : item.getSockets()) {
			assertThat(socket.getGem()).isNotNull();
		}
	}

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		playerProfile.setEquipment(getEquipment());
	}
}
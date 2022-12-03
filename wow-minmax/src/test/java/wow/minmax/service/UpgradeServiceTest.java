package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.character.BuildId;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.ItemSocket;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;

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
		profile.setEquipment(getEquipment());
		profile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		profile.getEquipment().set(null, ItemSlot.CHEST);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.CHEST, profile.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesRings() {
		profile.setEquipment(getEquipment());
		profile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		profile.getEquipment().set(null, ItemSlot.FINGER_1);
		profile.getEquipment().set(null, ItemSlot.FINGER_2);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.FINGERS, profile.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesTrinkets() {
		profile.setEquipment(getEquipment());
		profile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		profile.getEquipment().set(null, ItemSlot.TRINKET_1);
		profile.getEquipment().set(null, ItemSlot.TRINKET_2);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.TRINKETS, profile.getDamagingSpell());

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesWeapons() {
		profile.setEquipment(getEquipment());
		profile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		profile.getEquipment().set(null, ItemSlot.MAIN_HAND);
		profile.getEquipment().set(null, ItemSlot.OFF_HAND);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.WEAPONS, profile.getDamagingSpell());

		assertThat(upgrades).hasSize(3);
	}

	@Test
	void getBestItemVariant() {
		profile.setEquipment(getEquipment());
		profile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		EquippableItem item = underTest.getBestItemVariant(profile, getItem("Sunfire Robe").getItem(), ItemSlot.CHEST, profile.getDamagingSpell());

		assertThat(item.getEnchant()).isNotNull();

		for (ItemSocket socket : item.getSockets()) {
			assertThat(socket.getGem()).isNotNull();
		}
	}

	PlayerProfile profile;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile(BuildId.DESTRO_SHADOW);
		profile.setEquipment(getEquipment());
	}
}
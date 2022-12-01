package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.ItemSocket;
import wow.commons.model.spells.SpellId;
import wow.minmax.WowMinMaxTestConfig;
import wow.minmax.model.BuildIds;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.repository.PlayerProfileRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static wow.commons.model.character.BuffSetId.*;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowMinMaxTestConfig.class)
@TestPropertySource("classpath:application.properties")
class UpgradeServiceTest extends ServiceTest {
	@Autowired
	UpgradeService underTest;

	@MockBean
	PlayerProfileRepository playerProfileRepository;

	@Test
	void findUpgradesChest() {
		profile.setEquipment(getEquipment());
		profile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		profile.getEquipment().set(null, ItemSlot.CHEST);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.CHEST, SpellId.SHADOW_BOLT);

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesRings() {
		profile.setEquipment(getEquipment());
		profile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		profile.getEquipment().set(null, ItemSlot.FINGER_1);
		profile.getEquipment().set(null, ItemSlot.FINGER_2);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.FINGERS, SpellId.SHADOW_BOLT);

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesTrinkets() {
		profile.setEquipment(getEquipment());
		profile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		profile.getEquipment().set(null, ItemSlot.TRINKET_1);
		profile.getEquipment().set(null, ItemSlot.TRINKET_2);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.TRINKETS, SpellId.SHADOW_BOLT);

		assertThat(upgrades).hasSize(1);
	}

	@Test
	void findUpgradesWeapons() {
		profile.setEquipment(getEquipment());
		profile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		profile.getEquipment().set(null, ItemSlot.MAIN_HAND);
		profile.getEquipment().set(null, ItemSlot.OFF_HAND);

		List<Comparison> upgrades = underTest.findUpgrades(profile, ItemSlotGroup.WEAPONS, SpellId.SHADOW_BOLT);

		assertThat(upgrades).hasSize(3);
	}

	@Test
	void getBestItemVariant() {
		profile.setEquipment(getEquipment());
		profile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);

		EquippableItem item = underTest.getBestItemVariant(profile, getItem("Sunfire Robe").getItem(), ItemSlot.CHEST, SpellId.SHADOW_BOLT);

		assertThat(item.getEnchant()).isNotNull();

		for (ItemSocket socket : item.getSockets()) {
			assertThat(socket.getGem()).isNotNull();
		}
	}

	PlayerProfile profile;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile(BuildIds.DESTRO_SHADOW_BUILD);
		profile.setEquipment(getEquipment());

		when(playerProfileRepository.getPlayerProfileList()).thenReturn(List.of(profile));
		when(playerProfileRepository.getPlayerProfile(profile.getProfileId())).thenReturn(Optional.of(profile));
	}
}
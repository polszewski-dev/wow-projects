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
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.pve.Phase;
import wow.commons.model.unit.Build;
import wow.minmax.WowMinMaxTestConfig;
import wow.minmax.model.BuildIds;
import wow.minmax.model.PlayerProfile;
import wow.minmax.repository.PlayerProfileRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowMinMaxTestConfig.class)
@TestPropertySource("classpath:application.properties")
class PlayerProfileServiceTest extends ServiceTest {
	@Autowired
	PlayerProfileService underTest;

	@MockBean
	PlayerProfileRepository playerProfileRepository;

	@Test
	void getPlayerProfileList() {
		List<PlayerProfile> profiles = underTest.getPlayerProfileList();

		assertThat(profiles).hasSize(1);
		assertThat(profiles.get(0)).isSameAs(profile);

		verify(playerProfileRepository).getPlayerProfileList();
	}

	@Test
	void createPlayerProfile() {
		PlayerProfile newProfile = underTest.createPlayerProfile("New", Phase.TBC_P1);

		assertThat(newProfile.getProfileName()).isEqualTo("New");
		assertThat(newProfile.getPhase()).isEqualTo(Phase.TBC_P1);

		verify(playerProfileRepository).saveProfile(newProfile);
	}

	@Test
	void copyPlayerProfile() {
		PlayerProfile copy = underTest.copyPlayerProfile(profile.getProfileId(), "Copy", Phase.TBC_P1);

		assertThat(copy.getProfileName()).isEqualTo("Copy");
		assertThat(copy.getPhase()).isEqualTo(Phase.TBC_P1);

		verify(playerProfileRepository).saveProfile(copy);
	}

	@Test
	void getPlayerProfile() {
		PlayerProfile returnedProfile = underTest.getPlayerProfile(profile.getProfileId());

		assertThat(returnedProfile).isSameAs(profile);
	}

	@Test
	void changeItem() {
		assertThat(profile.getEquipment().getOffHand().getName()).isEqualTo("Chronicle of Dark Secrets");

		underTest.changeItem(profile.getProfileId(), ItemSlot.OFF_HAND, 34179);

		assertThat(profile.getEquipment().getOffHand().getName()).isEqualTo("Heart of the Pit");

		verify(playerProfileRepository).saveProfile(profile);
	}

	@Test
	void changeTwoHander() {
		assertThat(profile.getEquipment().getMainHand().getName()).isEqualTo("Sunflare");
		assertThat(profile.getEquipment().getOffHand().getName()).isEqualTo("Chronicle of Dark Secrets");

		underTest.changeItem(profile.getProfileId(), ItemSlot.MAIN_HAND, 34182);

		assertThat(profile.getEquipment().getMainHand().getName()).isEqualTo("Grand Magister's Staff of Torrents");
		assertThat(profile.getEquipment().getOffHand()).isNull();

		EquippableItem mainHand = profile.getEquipment().getMainHand();

		assertThat(mainHand.getEnchant()).isNotNull();

		for (int i = 1; i <= mainHand.getSocketCount(); ++i) {
			assertThat(mainHand.getGem(i)).isNotNull();
		}

		verify(playerProfileRepository).saveProfile(profile);
	}

	@Test
	void changeEnchant() {
		EquippableItem mainHand = profile.getEquipment().getMainHand();

		assertThat(mainHand.getEnchant().getName()).isEqualTo("Enchant Weapon – Soulfrost");

		underTest.changeEnchant(profile.getProfileId(), ItemSlot.MAIN_HAND, 2669);

		assertThat(mainHand.getEnchant().getName()).isEqualTo("Enchant Weapon - Major Spellpower");

		verify(playerProfileRepository).saveProfile(profile);
	}

	@Test
	void changeGem() {
		EquippableItem chest = profile.getEquipment().getChest();

		assertThat(chest.getName()).isEqualTo("Sunfire Robe");
		assertThat(chest.getSocketCount()).isEqualTo(3);
		assertThat(chest.getGem(2).getId()).isNotEqualTo(35761);

		underTest.changeGem(profile.getProfileId(), ItemSlot.CHEST, 2, 35761);

		assertThat(chest.getGem(2).getId()).isEqualTo(35761);

		verify(playerProfileRepository).saveProfile(profile);
	}

	@Test
	void resetEquipment() {
		underTest.resetEquipment(profile.getProfileId());

		for (ItemSlot slot : ItemSlot.getDpsSlots()) {
			assertThat(profile.getEquipment().get(slot)).isNull();
		}

		verify(playerProfileRepository).saveProfile(profile);
	}

	@Test
	void enableBuff() {
		profile.setBuffs(Build.BuffSetId.CONSUMES);

		assertThat(profile.getBuffs().stream().anyMatch(buff -> buff.getId() == 17628)).isFalse();
		assertThat(profile.getBuffs().stream().anyMatch(buff -> buff.getId() == 28540)).isTrue();

		underTest.enableBuff(profile.getProfileId(), 17628, true);

		assertThat(profile.getBuffs().stream().anyMatch(buff -> buff.getId() == 17628)).isTrue();
		assertThat(profile.getBuffs().stream().anyMatch(buff -> buff.getId() == 28540)).isFalse();

		verify(playerProfileRepository).saveProfile(profile);
	}

	@Test
	void disableBuff() {
		profile.setBuffs(Build.BuffSetId.CONSUMES);

		assertThat(profile.getBuffs().stream().anyMatch(buff -> buff.getId() == 28540)).isTrue();

		underTest.enableBuff(profile.getProfileId(), 28540, false);

		assertThat(profile.getBuffs().stream().anyMatch(buff -> buff.getId() == 28540)).isFalse();

		verify(playerProfileRepository).saveProfile(profile);
	}

	@Test
	void getBuild() {
		Build build = underTest.getBuild(BuildIds.DESTRO_SHADOW_BUILD, profile.getLevel());

		assertThat(build.getBuildId()).isEqualTo(BuildIds.DESTRO_SHADOW_BUILD);
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
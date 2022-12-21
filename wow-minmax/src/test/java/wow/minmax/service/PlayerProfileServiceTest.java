package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import wow.character.model.build.BuffSetId;
import wow.commons.model.categorization.ItemSlot;
import wow.minmax.converter.persistent.PlayerProfilePOConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.persistent.EquippableItemPO;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.PlayerProfileRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class PlayerProfileServiceTest extends ServiceTest {
	@Autowired
	PlayerProfileService underTest;

	@MockBean
	PlayerProfileRepository playerProfileRepository;


	@Autowired
	PlayerProfilePOConverter playerProfilePOConverter;

	@Captor
	ArgumentCaptor<PlayerProfilePO> profilePOCaptor;

	@Test
	void getPlayerProfileList() {
		List<PlayerProfile> profiles = underTest.getPlayerProfileList();

		assertThat(profiles).hasSize(1);
		assertThat(profiles.get(0).getProfileId()).isEqualTo(profilePO.getProfileId());

		verify(playerProfileRepository).getPlayerProfileList();
	}

	@Test
	void createPlayerProfile() {
		PlayerProfile newProfile = underTest.createPlayerProfile("New", PHASE);

		assertThat(newProfile.getProfileName()).isEqualTo("New");
		assertThat(newProfile.getPhase()).isEqualTo(PHASE);

		verify(playerProfileRepository).saveProfile(any());
	}

	@Test
	void copyPlayerProfile() {
		PlayerProfile copy = underTest.copyPlayerProfile(profilePO.getProfileId(), "Copy", PHASE);

		assertThat(copy.getProfileName()).isEqualTo("Copy");
		assertThat(copy.getPhase()).isEqualTo(PHASE);

		verify(playerProfileRepository).saveProfile(any());
	}

	@Test
	void getPlayerProfileById() {
		PlayerProfile returnedProfile = underTest.getPlayerProfile(profilePO.getProfileId());

		assertThat(returnedProfile.getProfileId()).isEqualTo(profilePO.getProfileId());
	}

	@Test
	void changeItem() {
		assertThat(profilePO.getEquipment().getOffHand().getItem().getName()).isEqualTo("Chronicle of Dark Secrets");

		underTest.changeItem(profilePO.getProfileId(), ItemSlot.OFF_HAND, 34179);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getEquipment().getOffHand().getItem().getName()).isEqualTo("Heart of the Pit");
	}

	@Test
	void changeTwoHander() {
		assertThat(profilePO.getEquipment().getMainHand().getItem().getName()).isEqualTo("Sunflare");
		assertThat(profilePO.getEquipment().getOffHand().getItem().getName()).isEqualTo("Chronicle of Dark Secrets");

		underTest.changeItem(profilePO.getProfileId(), ItemSlot.MAIN_HAND, 34182);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getEquipment().getMainHand().getItem().getName()).isEqualTo("Grand Magister's Staff of Torrents");
		assertThat(savedProfile.getEquipment().getOffHand()).isNull();

		EquippableItemPO mainHand = savedProfile.getEquipment().getMainHand();

		assertThat(mainHand.getEnchant()).isNotNull();

		for (int i = 0; i < mainHand.getSocketCount(); ++i) {
			assertThat(mainHand.getGems().get(i)).isNotNull();
		}
	}

	@Test
	void changeEnchant() {
		assertThat(profilePO.getEquipment().getMainHand().getEnchant().getName()).isEqualTo("Enchant Weapon – Soulfrost");

		underTest.changeEnchant(profilePO.getProfileId(), ItemSlot.MAIN_HAND, 2669);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getEquipment().getMainHand().getEnchant().getName()).isEqualTo("Enchant Weapon - Major Spellpower");
	}

	@Test
	void changeGem() {
		EquippableItemPO chest = profilePO.getEquipment().getChest();

		assertThat(chest.getItem().getName()).isEqualTo("Sunfire Robe");
		assertThat(chest.getSocketCount()).isEqualTo(3);
		assertThat(chest.getGems().get(1).getId()).isNotEqualTo(35761);

		underTest.changeGem(profilePO.getProfileId(), ItemSlot.CHEST, 1, 35761);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getEquipment().getChest().getGems().get(1).getId()).isEqualTo(35761);
	}

	@Test
	void resetEquipment() {
		underTest.resetEquipment(profilePO.getProfileId());

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getEquipment()).hasAllNullFieldsOrProperties();
	}

	@Test
	void enableBuff() {
		assertThat(profilePO.getBuffs().stream().anyMatch(buff -> buff.getId() == 17628)).isFalse();
		assertThat(profilePO.getBuffs().stream().anyMatch(buff -> buff.getId() == 28540)).isTrue();

		underTest.enableBuff(profilePO.getProfileId(), 17628, true);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getBuffs().stream().anyMatch(buff -> buff.getId() == 17628)).isTrue();
		assertThat(savedProfile.getBuffs().stream().anyMatch(buff -> buff.getId() == 28540)).isFalse();
	}

	@Test
	void disableBuff() {
		assertThat(profilePO.getBuffs().stream().anyMatch(buff -> buff.getId() == 28540)).isTrue();

		underTest.enableBuff(profilePO.getProfileId(), 28540, false);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getBuffs().stream().anyMatch(buff -> buff.getId() == 28540)).isFalse();
	}

	PlayerProfilePO profilePO;

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		profile.setEquipment(getEquipment());
		profile.setBuffs(BuffSetId.CONSUMES);

		profilePO = playerProfilePOConverter.convert(profile, Map.of());

		when(playerProfileRepository.getPlayerProfileList()).thenReturn(List.of(profilePO));
		when(playerProfileRepository.getPlayerProfile(profile.getProfileId())).thenReturn(Optional.of(profilePO));
	}
}
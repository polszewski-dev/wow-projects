package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import wow.character.model.build.BuffSetId;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.minmax.converter.persistent.PlayerProfilePOConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.persistent.EquippableItemPO;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.PlayerProfileRepository;

import java.util.List;
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
		List<PlayerProfileInfo> profiles = underTest.getPlayerProfileInfos();

		assertThat(profiles).hasSize(1);
		assertThat(profiles.get(0).getProfileId()).isEqualTo(profile.getProfileId());

		verify(playerProfileRepository).getPlayerProfileList();
	}

	@Test
	void createPlayerProfile() {
		PlayerProfile newProfile = underTest.createPlayerProfile(profile.getProfileInfo());

		assertThat(newProfile.getProfileName()).isEqualTo(profile.getProfileName());
		assertThat(newProfile.getPhase()).isEqualTo(profile.getPhase());
		assertThat(newProfile.getProfileId()).isNotNull();
		assertThat(newProfile.getLastModified()).isNotNull();

		verify(playerProfileRepository).saveProfile(any());
	}

	@Test
	void getPlayerProfileById() {
		PlayerProfile returnedProfile = underTest.getPlayerProfile(profile.getProfileId());

		assertThat(returnedProfile.getProfileId()).isEqualTo(profile.getProfileId());
	}

	@Test
	void changeItem() {
		assertThat(profile.getEquipment().getOffHand().getItem().getName()).isEqualTo("Chronicle of Dark Secrets");

		underTest.changeItemBestVariant(profile.getProfileId(), ItemSlot.OFF_HAND, 34179);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getEquipment().getOffHand().getItem().getName()).isEqualTo("Heart of the Pit");
	}

	@Test
	void changeTwoHander() {
		assertThat(profile.getEquipment().getMainHand().getItem().getName()).isEqualTo("Sunflare");
		assertThat(profile.getEquipment().getOffHand().getItem().getName()).isEqualTo("Chronicle of Dark Secrets");

		underTest.changeItemBestVariant(profile.getProfileId(), ItemSlot.MAIN_HAND, 34182);

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
		EquippableItem mainHand = profile.getEquipment().getMainHand();

		assertThat(mainHand.getEnchant().getName()).isEqualTo("Enchant Weapon – Soulfrost");

		Enchant enchant = itemRepository.getEnchant("Enchant Weapon - Major Spellpower", profile.getPhase()).orElseThrow();

		mainHand = mainHand.copy();
		mainHand.enchant(enchant);

		underTest.changeItem(profile.getProfileId(), ItemSlot.MAIN_HAND, mainHand);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getEquipment().getMainHand().getEnchant().getName()).isEqualTo("Enchant Weapon - Major Spellpower");
	}

	@Test
	void changeGem() {
		EquippableItem chest = profile.getEquipment().getChest();

		assertThat(chest.getItem().getName()).isEqualTo("Sunfire Robe");
		assertThat(chest.getSocketCount()).isEqualTo(3);
		assertThat(chest.getGems().get(1).getId()).isNotEqualTo(35761);

		Gem gem = itemRepository.getGem(35761, profile.getPhase()).orElseThrow();

		chest = chest.copy();
		chest.getSockets().insertGem(1, gem);

		underTest.changeItem(profile.getProfileId(), ItemSlot.CHEST, chest);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getEquipment().getChest().getGems().get(1).getId()).isEqualTo(35761);
	}

	@Test
	void resetEquipment() {
		underTest.resetEquipment(profile.getProfileId());

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getEquipment()).hasAllNullFieldsOrProperties();
	}

	@Test
	void enableBuff() {
		assertThat(profile.getBuffs().hasBuff(17628)).isFalse();
		assertThat(profile.getBuffs().hasBuff(28540)).isTrue();

		underTest.enableBuff(profile.getProfileId(), 17628, true);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getBuffs().stream().anyMatch(buff -> buff.getId() == 17628)).isTrue();
		assertThat(savedProfile.getBuffs().stream().anyMatch(buff -> buff.getId() == 28540)).isFalse();
	}

	@Test
	void disableBuff() {
		assertThat(profile.getBuffs().hasBuff(28540)).isTrue();

		underTest.enableBuff(profile.getProfileId(), 28540, false);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();

		assertThat(savedProfile.getBuffs().stream().anyMatch(buff -> buff.getId() == 28540)).isFalse();
	}

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		profile.setEquipment(getEquipment());
		profile.setBuffs(BuffSetId.CONSUMES);

		PlayerProfilePO profilePO = playerProfilePOConverter.convert(profile);

		when(playerProfileRepository.getPlayerProfileList()).thenReturn(List.of(profilePO));
		when(playerProfileRepository.getPlayerProfile(profile.getProfileId())).thenReturn(Optional.of(profilePO));
	}
}
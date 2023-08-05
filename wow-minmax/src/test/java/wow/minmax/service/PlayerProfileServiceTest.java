package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffCategory;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.minmax.converter.persistent.PlayerProfilePOConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.persistent.CharacterPO;
import wow.minmax.model.persistent.EquippableItemPO;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.PlayerProfileRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static wow.character.model.character.BuffListType.CHARACTER_BUFF;
import static wow.commons.model.buffs.BuffId.FLASK_OF_PURE_DEATH;
import static wow.commons.model.buffs.BuffId.FLASK_OF_SUPREME_POWER;
import static wow.commons.model.categorization.ItemSlot.*;

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
		assertThat(newProfile.getCharacterClassId()).isEqualTo(profile.getCharacterClassId());
		assertThat(newProfile.getRaceId()).isEqualTo(profile.getRaceId());
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
		assertThat(character.getEquipment().getOffHand().getItem().getName()).isEqualTo("Chronicle of Dark Secrets");

		underTest.changeItemBestVariant(CHARACTER_KEY, OFF_HAND, 34179);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();
		CharacterPO savedCharacter = getSavedCharacter(savedProfile);

		assertThat(savedCharacter.getEquipment().getItemsBySlot().get(OFF_HAND).getItem().getName()).isEqualTo("Heart of the Pit");
	}

	@Test
	void changeTwoHander() {
		assertThat(character.getEquipment().getMainHand().getItem().getName()).isEqualTo("Sunflare");
		assertThat(character.getEquipment().getOffHand().getItem().getName()).isEqualTo("Chronicle of Dark Secrets");

		underTest.changeItemBestVariant(CHARACTER_KEY, MAIN_HAND, 34182);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();
		CharacterPO savedCharacter = getSavedCharacter(savedProfile);

		assertThat(savedCharacter.getEquipment().getItemsBySlot().get(MAIN_HAND).getItem().getName()).isEqualTo("Grand Magister's Staff of Torrents");
		assertThat(savedCharacter.getEquipment().getItemsBySlot().get(OFF_HAND)).isNull();

		EquippableItemPO mainHand = savedCharacter.getEquipment().getItemsBySlot().get(MAIN_HAND);

		assertThat(mainHand.getEnchant()).isNotNull();

		for (int i = 0; i < mainHand.getSocketCount(); ++i) {
			assertThat(mainHand.getGems().get(i)).isNotNull();
		}
	}

	@Test
	void changeEnchant() {
		EquippableItem mainHand = character.getEquipment().getMainHand();

		assertThat(mainHand.getEnchant().getName()).isEqualTo("Enchant Weapon - Soulfrost");

		Enchant enchant = itemRepository.getEnchant("Enchant Weapon - Major Spellpower", character.getPhaseId()).orElseThrow();

		mainHand = mainHand.copy();
		mainHand.enchant(enchant);

		underTest.changeItem(CHARACTER_KEY, MAIN_HAND, mainHand);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();
		CharacterPO savedCharacter = getSavedCharacter(savedProfile);

		assertThat(savedCharacter.getEquipment().getItemsBySlot().get(MAIN_HAND).getEnchant().getName()).isEqualTo("Enchant Weapon - Major Spellpower");
	}

	@Test
	void changeGem() {
		EquippableItem chest = character.getEquipment().getChest();

		assertThat(chest.getItem().getName()).isEqualTo("Sunfire Robe");
		assertThat(chest.getSocketCount()).isEqualTo(3);
		assertThat(chest.getGems().get(1).getId()).isNotEqualTo(35761);

		Gem gem = itemRepository.getGem(35761, character.getPhaseId()).orElseThrow();

		chest = chest.copy();
		chest.getSockets().insertGem(1, gem);

		underTest.changeItem(CHARACTER_KEY, CHEST, chest);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();
		CharacterPO savedCharacter = getSavedCharacter(savedProfile);

		assertThat(savedCharacter.getEquipment().getItemsBySlot().get(CHEST).getGems().get(1).getId()).isEqualTo(35761);
	}

	@Test
	void resetEquipment() {
		underTest.resetEquipment(CHARACTER_KEY);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();
		CharacterPO savedCharacter = getSavedCharacter(savedProfile);

		assertThat(savedCharacter.getEquipment().getItemsBySlot()).isEmpty();
	}

	@Test
	void enableBuff() {
		assertThat(character.getBuffs().has(FLASK_OF_SUPREME_POWER)).isFalse();
		assertThat(character.getBuffs().has(FLASK_OF_PURE_DEATH)).isTrue();

		underTest.enableBuff(CHARACTER_KEY, CHARACTER_BUFF, FLASK_OF_SUPREME_POWER, 0, true);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();
		CharacterPO savedCharacter = getSavedCharacter(savedProfile);

		assertThat(savedCharacter.getBuffs().stream().anyMatch(buff -> buff.getBuffId() == FLASK_OF_SUPREME_POWER)).isTrue();
		assertThat(savedCharacter.getBuffs().stream().anyMatch(buff -> buff.getBuffId() == FLASK_OF_PURE_DEATH)).isFalse();
	}

	@Test
	void disableBuff() {
		assertThat(character.getBuffs().has(FLASK_OF_PURE_DEATH)).isTrue();

		underTest.enableBuff(CHARACTER_KEY, CHARACTER_BUFF, FLASK_OF_PURE_DEATH, 0, false);

		verify(playerProfileRepository).saveProfile(profilePOCaptor.capture());
		PlayerProfilePO savedProfile = profilePOCaptor.getValue();
		CharacterPO savedCharacter = getSavedCharacter(savedProfile);

		assertThat(savedCharacter.getBuffs().stream().anyMatch(buff -> buff.getBuffId() == FLASK_OF_PURE_DEATH)).isFalse();
	}

	CharacterPO getSavedCharacter(PlayerProfilePO savedProfile) {
		return savedProfile.getCharacterByKey().get(CHARACTER_KEY);
	}

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		character.setEquipment(getEquipment());

		var consumes = character.getBuffs().getList().stream()
				.filter(x -> x.getCategories().contains(BuffCategory.CONSUME))
				.map(Buff::getId)
				.toList();
		character.setBuffs(consumes);

		PlayerProfilePO profilePO = playerProfilePOConverter.convert(profile);

		when(playerProfileRepository.getPlayerProfileList()).thenReturn(List.of(profilePO));
		when(playerProfileRepository.getPlayerProfile(profile.getProfileId())).thenReturn(Optional.of(profilePO));
	}
}
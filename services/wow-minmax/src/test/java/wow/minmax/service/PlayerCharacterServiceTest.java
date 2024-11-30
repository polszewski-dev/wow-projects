package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffCategory;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.minmax.converter.persistent.PlayerCharacterPOConverter;
import wow.minmax.converter.persistent.PlayerProfilePOConverter;
import wow.minmax.model.persistent.EquippableItemPO;
import wow.minmax.model.persistent.PlayerCharacterPO;
import wow.minmax.repository.PlayerCharacterRepository;
import wow.minmax.repository.PlayerProfileRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static wow.character.model.character.BuffListType.CHARACTER_BUFF;
import static wow.commons.model.buff.BuffId.FLASK_OF_PURE_DEATH;
import static wow.commons.model.buff.BuffId.FLASK_OF_SUPREME_POWER;
import static wow.commons.model.categorization.ItemSlot.*;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class PlayerCharacterServiceTest extends ServiceTest {
	@Autowired
	PlayerCharacterService underTest;

	@Autowired
	PlayerProfileRepository playerProfileRepository;

	@Autowired
	PlayerCharacterRepository playerCharacterRepository;

	@Autowired
	PlayerProfilePOConverter playerProfilePOConverter;

	@Autowired
	PlayerCharacterPOConverter playerCharacterPOConverter;

	@Captor
	ArgumentCaptor<PlayerCharacterPO> characterPOCaptor;

	@Test
	void changeItem() {
		assertThat(character.getEquipment().getOffHand().getItem().getName()).isEqualTo("Chronicle of Dark Secrets");

		var item = getItem("Heart of the Pit");

		underTest.equipItem(CHARACTER_KEY, OFF_HAND, item, true, GemFilter.empty());

		verify(playerCharacterRepository).save(characterPOCaptor.capture());

		var savedCharacter = characterPOCaptor.getValue();

		assertThat(savedCharacter.getEquipment().getItemsBySlot().get(OFF_HAND).getItem().getName()).isEqualTo("Heart of the Pit");
	}

	@Test
	void changeTwoHander() {
		assertThat(character.getEquipment().getMainHand().getItem().getName()).isEqualTo("Sunflare");
		assertThat(character.getEquipment().getOffHand().getItem().getName()).isEqualTo("Chronicle of Dark Secrets");

		var item = getItem("Grand Magister's Staff of Torrents");

		underTest.equipItem(CHARACTER_KEY, MAIN_HAND, item, true, GemFilter.empty());

		verify(playerCharacterRepository).save(characterPOCaptor.capture());

		var savedCharacter = characterPOCaptor.getValue();

		assertThat(savedCharacter.getEquipment().getItemsBySlot().get(MAIN_HAND).getItem().getName()).isEqualTo("Grand Magister's Staff of Torrents");
		assertThat(savedCharacter.getEquipment().getItemsBySlot().get(OFF_HAND)).isNull();

		EquippableItemPO mainHand = savedCharacter.getEquipment().getItemsBySlot().get(MAIN_HAND);

		assertThat(mainHand.getEnchant()).isNotNull();

		for (var gem : mainHand.getGems()) {
			assertThat(gem).isNotNull();
		}
	}

	@Test
	void changeEnchant() {
		EquippableItem mainHand = character.getEquipment().getMainHand();

		assertThat(mainHand.getEnchant().getName()).isEqualTo("Enchant Weapon - Soulfrost");

		Enchant enchant = enchantRepository.getEnchant("Enchant Weapon - Major Spellpower", character.getPhaseId()).orElseThrow();

		mainHand = mainHand.copy();
		mainHand.enchant(enchant);

		underTest.equipItem(CHARACTER_KEY, MAIN_HAND, mainHand);

		verify(playerCharacterRepository).save(characterPOCaptor.capture());

		var savedCharacter = characterPOCaptor.getValue();

		assertThat(savedCharacter.getEquipment().getItemsBySlot().get(MAIN_HAND).getEnchant().getName()).isEqualTo("Enchant Weapon - Major Spellpower");
	}

	@Test
	void changeGem() {
		EquippableItem chest = character.getEquipment().getChest();

		assertThat(chest.getItem().getName()).isEqualTo("Sunfire Robe");
		assertThat(chest.getSocketCount()).isEqualTo(3);
		assertThat(chest.getGems().get(1).getId()).isNotEqualTo(35761);

		Gem gem = gemRepository.getGem(35761, character.getPhaseId()).orElseThrow();

		chest = chest.copy();
		chest.getSockets().insertGem(1, gem);

		underTest.equipItem(CHARACTER_KEY, CHEST, chest);

		verify(playerCharacterRepository).save(characterPOCaptor.capture());

		var savedCharacter = characterPOCaptor.getValue();

		assertThat(savedCharacter.getEquipment().getItemsBySlot().get(CHEST).getGems().get(1).getId()).isEqualTo(35761);
	}

	@Test
	void resetEquipment() {
		underTest.resetEquipment(CHARACTER_KEY);

		verify(playerCharacterRepository).save(characterPOCaptor.capture());

		var savedCharacter = characterPOCaptor.getValue();

		assertThat(savedCharacter.getEquipment().getItemsBySlot()).isEmpty();
	}

	@Test
	void enableBuff() {
		assertThat(character.getBuffs().has(FLASK_OF_SUPREME_POWER)).isFalse();
		assertThat(character.getBuffs().has(FLASK_OF_PURE_DEATH)).isTrue();

		underTest.enableBuff(CHARACTER_KEY, CHARACTER_BUFF, FLASK_OF_SUPREME_POWER, 0, true);

		verify(playerCharacterRepository).save(characterPOCaptor.capture());

		var savedCharacter = characterPOCaptor.getValue();

		assertThat(savedCharacter.getBuffs().stream().anyMatch(buff -> buff.getBuffId() == FLASK_OF_SUPREME_POWER)).isTrue();
		assertThat(savedCharacter.getBuffs().stream().anyMatch(buff -> buff.getBuffId() == FLASK_OF_PURE_DEATH)).isFalse();
	}

	@Test
	void disableBuff() {
		assertThat(character.getBuffs().has(FLASK_OF_PURE_DEATH)).isTrue();

		underTest.enableBuff(CHARACTER_KEY, CHARACTER_BUFF, FLASK_OF_PURE_DEATH, 0, false);

		verify(playerCharacterRepository).save(characterPOCaptor.capture());

		var savedCharacter = characterPOCaptor.getValue();

		assertThat(savedCharacter.getBuffs().stream().anyMatch(buff -> buff.getBuffId() == FLASK_OF_PURE_DEATH)).isFalse();
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

		var characterPO = playerCharacterPOConverter.convert(character, CHARACTER_KEY);
		var profilePO = playerProfilePOConverter.convert(profile);

		when(playerProfileRepository.findAll()).thenReturn(List.of(profilePO));
		when(playerProfileRepository.findById(profile.getProfileId().toString())).thenReturn(Optional.of(profilePO));

		when(playerCharacterRepository.findAll()).thenReturn(List.of(characterPO));
		when(playerCharacterRepository.findById(profile.getProfileId().toString())).thenReturn(Optional.of(characterPO));
	}
}
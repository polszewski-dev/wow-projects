package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffCategory;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static wow.character.model.character.BuffListType.CHARACTER_BUFF;
import static wow.commons.model.buff.BuffId.FLASK_OF_PURE_DEATH;
import static wow.commons.model.buff.BuffId.FLASK_OF_SUPREME_POWER;
import static wow.commons.model.categorization.ItemSlot.*;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
class PlayerCharacterServiceTest extends ServiceTest {
	@Test
	void changeItem() {
		assertItem(OFF_HAND, "Heart of the Pit");

		var item = getItem("Chronicle of Dark Secrets");

		underTest.equipItem(CHARACTER_KEY, OFF_HAND, item, true, GemFilter.empty());

		assertItem(OFF_HAND, "Chronicle of Dark Secrets");
	}

	@Test
	void changeTwoHander() {
		assertItem(MAIN_HAND, "Sunflare");
		assertItem(OFF_HAND, "Heart of the Pit");

		var item = getItem("Grand Magister's Staff of Torrents");

		underTest.equipItem(CHARACTER_KEY, MAIN_HAND, item, true, GemFilter.empty());

		assertItem(MAIN_HAND, "Grand Magister's Staff of Torrents");
		assertItem(OFF_HAND, null);
	}

	@Test
	void changeEnchant() {
		assertEnchant(MAIN_HAND, "Enchant Weapon - Soulfrost");

		var enchant = enchantRepository.getEnchant("Enchant Weapon - Major Spellpower", character.getPhaseId()).orElseThrow();

		var mainHand = character.getEquippedItem(MAIN_HAND)
				.copy()
				.enchant(enchant);

		underTest.equipItem(CHARACTER_KEY, MAIN_HAND, mainHand);

		assertEnchant(MAIN_HAND, "Enchant Weapon - Major Spellpower");
	}

	@Test
	void changeGem() {
		assertItem(CHEST, "Sunfire Robe");
		assertGem(CHEST, 1, "Runed Crimson Spinel");

		var gem = gemRepository.getGem("Forceful Seaspray Emerald", character.getPhaseId()).orElseThrow();

		var chest = character.getEquippedItem(CHEST).copy();

		chest.getSockets().insertGem(1, gem);

		underTest.equipItem(CHARACTER_KEY, CHEST, chest);

		assertGem(CHEST, 1, "Forceful Seaspray Emerald");
	}

	@Test
	void resetEquipment() {
		underTest.resetEquipment(CHARACTER_KEY);

		assertThat(savedCharacter.getEquipment().getItemsBySlot()).isEmpty();
	}

	@Test
	void enableBuff() {
		assertBuffStatus(FLASK_OF_SUPREME_POWER, false);
		assertBuffStatus(FLASK_OF_PURE_DEATH, true);

		underTest.enableBuff(CHARACTER_KEY, CHARACTER_BUFF, FLASK_OF_SUPREME_POWER, 0, true);

		assertBuffStatus(FLASK_OF_SUPREME_POWER, true);
		assertBuffStatus(FLASK_OF_PURE_DEATH, false);
	}

	@Test
	void disableBuff() {
		assertBuffStatus(FLASK_OF_PURE_DEATH, true);

		underTest.enableBuff(CHARACTER_KEY, CHARACTER_BUFF, FLASK_OF_PURE_DEATH, 0, false);

		assertBuffStatus(FLASK_OF_PURE_DEATH, false);
	}

	@Autowired
	PlayerCharacterService underTest;

	@MockBean
	UpgradeService upgradeService;

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		when(upgradeService.getBestItemVariant(any(), any(), any(), any())).thenAnswer(input -> new EquippableItem(input.getArgument(1, Item.class)));
	}

	@Override
	void prepareCharacter() {
		var consumes = character.getBuffs().getList().stream()
				.filter(x -> x.getCategories().contains(BuffCategory.CONSUME))
				.map(Buff::getId)
				.toList();

		character.setBuffs(consumes);
	}

	private void assertItem(ItemSlot itemSlot, String expectedName) {
		var itemName = getItemName(itemSlot);

		assertThat(itemName).isEqualTo(expectedName);
	}

	private void assertEnchant(ItemSlot itemSlot, String expectedName) {
		var enchantName = getEnchantName(itemSlot);

		assertThat(enchantName).isEqualTo(expectedName);
	}

	private void assertGem(ItemSlot itemSlot, int socketNo, String expectedName) {
		var gemName = getGemName(itemSlot, socketNo);

		assertThat(gemName).isEqualTo(expectedName);
	}

	private String getItemName(ItemSlot itemSlot) {
		var equippableItemConfig = savedCharacter.getEquipment().getItemsBySlot().get(itemSlot);

		if (equippableItemConfig == null) {
			return null;
		}

		return equippableItemConfig.getItem().getName();
	}

	private String getEnchantName(ItemSlot itemSlot) {
		var equippableItemConfig = savedCharacter.getEquipment().getItemsBySlot().get(itemSlot);

		if (equippableItemConfig == null) {
			return null;
		}

		var enchant = equippableItemConfig.getEnchant();

		if (enchant == null) {
			return null;
		}

		return enchant.getName();
	}

	private String getGemName(ItemSlot itemSlot, int socketNo) {
		var equippableItemConfig = savedCharacter.getEquipment().getItemsBySlot().get(itemSlot);

		if (equippableItemConfig == null) {
			return null;
		}

		var gem = equippableItemConfig.getGems().get(socketNo);

		if (gem == null) {
			return null;
		}

		return gem.getName();
	}

	private void assertBuffStatus(BuffId buffId, boolean enabled) {
		assertThat(savedCharacter.getBuffs().stream().anyMatch(buff -> buff.getBuffId() == buffId)).isEqualTo(enabled);
	}
}

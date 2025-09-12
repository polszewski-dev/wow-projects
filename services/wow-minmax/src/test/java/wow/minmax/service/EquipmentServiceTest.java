package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.minmax.model.equipment.SocketStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static wow.commons.model.attribute.AttributeId.POWER;
import static wow.commons.model.attribute.condition.MiscCondition.SPELL;
import static wow.commons.model.categorization.ItemSlot.*;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
class EquipmentServiceTest extends ServiceTest {
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
	void getEquipmentSocketStatusMatching() {
		var statuses = underTest.getEquipmentSocketStatus(CHARACTER_KEY);
		var status = statuses.socketStatusesByItemSlot().get(SHOULDER);
		var socketBonusStatus = status.socketBonusStatus();

		var expectedSocketStatuses = List.of(
				new SocketStatus(0, SocketType.BLUE, true),
				new SocketStatus(1, SocketType.YELLOW, true)
		);
		var expectedSocketBonus = Attributes.of(POWER, 4, SPELL);

		assertThat(status.socketStatuses()).isEqualTo(expectedSocketStatuses);
		assertThat(socketBonusStatus.bonus().getModifierComponent().attributes()).isEqualTo(expectedSocketBonus);
		assertThat(socketBonusStatus.enabled()).isTrue();
	}

	@Test
	void getEquipmentSocketStatusNonMatching() {
		var shoulder = character.getEquippedItem(SHOULDER).copy();
		var gem = shoulder.getSockets().getGem(1);// inserting orange gem into blue socket

		shoulder.getSockets().insertGem(0, gem);
		underTest.equipItem(CHARACTER_KEY, SHOULDER, shoulder);

		var statuses = underTest.getEquipmentSocketStatus(CHARACTER_KEY);
		var status = statuses.socketStatusesByItemSlot().get(SHOULDER);
		var socketBonusStatus = status.socketBonusStatus();

		var expectedSocketStatuses = List.of(
				new SocketStatus(0, SocketType.BLUE, false),
				new SocketStatus(1, SocketType.YELLOW, true)
		);
		var expectedSocketBonus = Attributes.of(POWER, 4, SPELL);

		assertThat(status.socketStatuses()).isEqualTo(expectedSocketStatuses);
		assertThat(socketBonusStatus.bonus().getModifierComponent().attributes()).isEqualTo(expectedSocketBonus);
		assertThat(socketBonusStatus.enabled()).isFalse();
	}

	@Autowired
	EquipmentService underTest;

	@MockBean
	UpgradeService upgradeService;

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		when(upgradeService.getBestItemVariant(any(), any(), any(), any())).thenAnswer(input -> new EquippableItem(input.getArgument(1, Item.class)));
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
}

package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.HEAD;

/**
 * User: POlszewski
 * Date: 2025-08-31
 */
class EquipmentOptionsServiceTest extends ServiceTest {
	@Test
	void getEquipmentOptions() {
		var equipmentOptions =  underTest.getEquipmentOptions(PLAYER_ID);

		assertThat(equipmentOptions.editGems()).isTrue();
		assertThat(equipmentOptions.heroics()).isTrue();
	}

	@Test
	void getItemOptions() {
		var itemOptions = underTest.getItemOptions(PLAYER_ID, HEAD);

		assertThat(itemOptions.itemSlot()).isEqualTo(HEAD);
		assertThat(itemOptions.items()).hasSize(144);
	}

	@Test
	void getEnchantOptions() {
		var enchantOptions = underTest.getEnchantOptions(PLAYER_ID);

		assertThat(enchantOptions).hasSize(19);
	}

	@Test
	void getGemOptions() {
		var gemOptions = underTest.getGemOptions(PLAYER_ID);

		assertThat(gemOptions).hasSize(4);
	}

	@Autowired
	EquipmentOptionsService underTest;
}
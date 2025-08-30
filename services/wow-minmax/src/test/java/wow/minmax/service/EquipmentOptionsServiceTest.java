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
		var equipmentOptions =  underTest.getEquipmentOptions(CHARACTER_KEY);

		assertThat(equipmentOptions.editGems()).isTrue();
		assertThat(equipmentOptions.heroics()).isTrue();
	}

	@Test
	void getItemOptions() {
		var itemOptions = underTest.getItemOptions(CHARACTER_KEY, HEAD);

		assertThat(itemOptions.itemSlot()).isEqualTo(HEAD);
		assertThat(itemOptions.items()).hasSize(144);
	}

	@Test
	void getEnchantOptions() {
		var enchantOptions = underTest.getEnchantOptions(CHARACTER_KEY);

		assertThat(enchantOptions).hasSize(19);
	}

	@Test
	void getGemOptions() {
		var gemOptions = underTest.getGemOptions(CHARACTER_KEY);

		assertThat(gemOptions).hasSize(4);
	}

	@Autowired
	EquipmentOptionsService underTest;
}
package wow.commons.client.converter.equipment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.equipment.EquippableItem;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.dto.equipment.EquippableItemDTO;
import wow.commons.model.item.EnchantId;
import wow.commons.model.item.GemId;
import wow.commons.model.item.ItemId;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.repository.item.GemRepository;
import wow.commons.repository.item.ItemRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class EquippableItemConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	EquippableItemConverter equippableItemConverter;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	EnchantRepository enchantRepository;

	@Autowired
	GemRepository gemRepository;

	@Test
	void convert() {
		var item = itemRepository.getItem(ItemId.of(34182), TBC_P5).orElseThrow();
		var enchant = enchantRepository.getEnchant(EnchantId.of(27982), TBC_P5).orElseThrow();
		var gem1 = gemRepository.getGem(GemId.of(32196), TBC_P5).orElseThrow();
		var gem2 = gemRepository.getGem(GemId.of(35760), TBC_P5).orElseThrow();
		var gem3 = gemRepository.getGem(GemId.of(32215), TBC_P5).orElseThrow();

		var equippableItem = new EquippableItem(item)
				.enchant(enchant)
				.gem(gem1, gem2, gem3);

		var converted = equippableItemConverter.convert(equippableItem);

		assertThat(converted).isEqualTo(
				new EquippableItemDTO(
						34182,
						27982,
						List.of(32196, 35760, 32215)
				)
		);
	}

	@Test
	void convertBack() {
		var equippableItem = new EquippableItemDTO(
				34182,
				27982,
				List.of(32196, 35760, 32215)
		);

		var converted = equippableItemConverter.convertBack(equippableItem, TBC_P5);

		assertId(converted.getItem(), 34182);
		assertId(converted.getEnchant(), 27982);
		assertId(converted.getGem(0), 32196);
		assertId(converted.getGem(1), 35760);
		assertId(converted.getGem(2), 32215);
	}
}
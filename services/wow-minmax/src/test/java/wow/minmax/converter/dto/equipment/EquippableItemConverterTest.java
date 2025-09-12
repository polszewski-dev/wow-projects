package wow.minmax.converter.dto.equipment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.equipment.EquippableItem;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.repository.item.GemRepository;
import wow.commons.repository.item.ItemRepository;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.client.dto.equipment.EnchantDTO;
import wow.minmax.client.dto.equipment.EquippableItemDTO;
import wow.minmax.client.dto.equipment.GemDTO;
import wow.minmax.client.dto.equipment.ItemDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class EquippableItemConverterTest extends WowMinMaxSpringTest {
	@Autowired
	EquippableItemConverter equippableItemConverter;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	EnchantRepository enchantRepository;

	@Autowired
	GemRepository gemRepository;

	@Autowired
	ItemConverter itemConverter;

	@Autowired
	EnchantConverter enchantConverter;

	@Autowired
	GemConverter gemConverter;

	@Test
	void convert() {
		var item = itemRepository.getItem(34182, TBC_P5).orElseThrow();
		var enchant = enchantRepository.getEnchant(27982, TBC_P5).orElseThrow();
		var gem1 = gemRepository.getGem(32196, TBC_P5).orElseThrow();
		var gem2 = gemRepository.getGem(35760, TBC_P5).orElseThrow();
		var gem3 = gemRepository.getGem(32215, TBC_P5).orElseThrow();

		var equippableItem = new EquippableItem(item)
				.enchant(enchant)
				.gem(gem1, gem2, gem3);

		var converted = equippableItemConverter.convert(equippableItem);

		assertThat(converted).isEqualTo(
				new EquippableItemDTO(
						itemConverter.convert(item),
						enchantConverter.convert(enchant),
						gemConverter.convertList(List.of(gem1, gem2, gem3))
				)
		);
	}

	@Test
	void convertBack() {
		var equippableItem = new EquippableItemDTO(
				new ItemDTO(34182, null, null, null, null, 0, null, null, null, null, null, null, null, null),
				new EnchantDTO(27982, null, null, null, null),
				List.of(
						new GemDTO(32196, null, null, null, null, null, null, null),
						new GemDTO(35760, null, null, null, null, null, null, null),
						new GemDTO(32215, null, null, null, null, null, null, null)
				)
		);

		var converted = equippableItemConverter.convertBack(equippableItem, TBC_P5);

		assertThat(converted.getItem().getId()).isEqualTo(34182);
		assertThat(converted.getEnchant().getId()).isEqualTo(27982);
		assertThat(converted.getGem(0).getId()).isEqualTo(32196);
		assertThat(converted.getGem(1).getId()).isEqualTo(35760);
		assertThat(converted.getGem(2).getId()).isEqualTo(32215);
	}
}
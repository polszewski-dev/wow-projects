package wow.commons.client.converter.equipment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.dto.equipment.EquipmentDTO;
import wow.commons.client.dto.equipment.EquippableItemDTO;
import wow.commons.repository.item.ItemRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.categorization.ItemSlot.TRINKET_2;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class EquipmentConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	EquipmentConverter equipmentConverter;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	EquippableItemConverter equippableItemConverter;

	@Test
	void convert() {
		var equipment = new Equipment();

		var trinket1 = new EquippableItem(itemRepository.getItem(32483, TBC_P5).orElseThrow());
		var trinket2 = new EquippableItem(itemRepository.getItem(33829, TBC_P5).orElseThrow());

		equipment.equip(trinket1, TRINKET_1);
		equipment.equip(trinket2, TRINKET_2);

		var converted = equipmentConverter.convert(equipment);

		assertThat(converted).isEqualTo(
				new EquipmentDTO(
						Map.ofEntries(
								Map.entry(TRINKET_1, equippableItemConverter.convert(trinket1)),
								Map.entry(TRINKET_2, equippableItemConverter.convert(trinket2))
						)
				)
		);
	}

	@Test
	void convertBack() {
		var trinket1 = new EquippableItemDTO(32483, null, List.of());
		var trinket2 = new EquippableItemDTO(33829, null, List.of());

		var equipment = new EquipmentDTO(Map.ofEntries(
				Map.entry(TRINKET_1, trinket1),
				Map.entry(TRINKET_2, trinket2)
		));


		var converted = equipmentConverter.convertBack(equipment, TBC_P5);

		assertThat(converted.getTrinket1().getId()).isEqualTo(32483);
		assertThat(converted.getTrinket2().getId()).isEqualTo(33829);
	}
}
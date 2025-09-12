package wow.minmax.converter.dto.equipment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.item.ItemId;
import wow.commons.repository.item.ItemRepository;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.client.dto.equipment.EquipmentDTO;
import wow.minmax.client.dto.equipment.EquippableItemDTO;
import wow.minmax.client.dto.equipment.ItemDTO;

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
class EquipmentConverterTest extends WowMinMaxSpringTest {
	@Autowired
	EquipmentConverter equipmentConverter;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	EquippableItemConverter equippableItemConverter;

	@Test
	void convert() {
		var equipment = new Equipment();

		var trinket1 = new EquippableItem(itemRepository.getItem(ItemId.of(32483), TBC_P5).orElseThrow());
		var trinket2 = new EquippableItem(itemRepository.getItem(ItemId.of(33829), TBC_P5).orElseThrow());

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
		var trinket1 = new EquippableItemDTO(new ItemDTO(32483, null, null, null, null, 0, null, null, null, null, null, null, null, null), null, List.of());
		var trinket2 = new EquippableItemDTO(new ItemDTO(33829, null, null, null, null, 0, null, null, null, null, null, null, null, null), null, List.of());

		var equipment = new EquipmentDTO(Map.ofEntries(
				Map.entry(TRINKET_1, trinket1),
				Map.entry(TRINKET_2, trinket2)
		));


		var converted = equipmentConverter.convertBack(equipment, TBC_P5);

		assertThat(converted.getTrinket1().getId()).isEqualTo(ItemId.of(32483));
		assertThat(converted.getTrinket2().getId()).isEqualTo(ItemId.of(33829));
	}
}
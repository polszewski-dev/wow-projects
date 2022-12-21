package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.EquipmentDTO;
import wow.minmax.model.dto.EquipmentSlotDTO;
import wow.minmax.model.dto.EquippableItemDTO;

import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class EquipmentConverter extends Converter<Equipment, EquipmentDTO> {
	private final EquippableItemConverter equippableItemConverter;

	@Override
	protected EquipmentDTO doConvert(Equipment equipment) {
		return new EquipmentDTO(
				ItemSlot.getDpsSlots().stream()
						.map(slot -> getEquipmentSlotDTO(slot, equipment))
						.collect(Collectors.toMap(EquipmentSlotDTO::getSlot, x -> x))
		);
	}

	private EquipmentSlotDTO getEquipmentSlotDTO(ItemSlot slot, Equipment equipment) {
		return new EquipmentSlotDTO(
				slot,
				ItemSlotGroup.getGroup(slot).orElseThrow(),
				convertItem(equipment, equipment.get(slot)),
				null
		);
	}

	public EquippableItemDTO convertItem(Equipment equipment, EquippableItem item) {
		EquippableItemDTO dto = equippableItemConverter.convert(item);
		setSocketMatchingInfo(equipment, item, dto);
		return dto;
	}

	private static void setSocketMatchingInfo(Equipment equipment, EquippableItem item, EquippableItemDTO dto) {
		if (dto == null) {
			return;
		}

		for (int i = 0; i < item.getSocketCount(); ++i) {
			dto.getSockets().get(i).setMatching(equipment.hasMatchingGem(item, i));
		}

		dto.setSocketBonusEnabled(equipment.allSocketsHaveMatchingGems(item));
	}
}

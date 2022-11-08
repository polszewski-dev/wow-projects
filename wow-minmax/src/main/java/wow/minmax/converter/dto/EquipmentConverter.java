package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.EquipmentDTO;
import wow.minmax.model.dto.EquippableItemDTO;

import java.util.Map;
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
		var itemsBySlot = equipment.toMap().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> convertItem(equipment, e.getValue())
				));
		return new EquipmentDTO(itemsBySlot);
	}

	public EquippableItemDTO convertItem(Equipment equipment, EquippableItem item) {
		EquippableItemDTO dto = equippableItemConverter.convert(item);
		if (dto != null) {
			dto.setSocket1Matching(equipment.isCompleteMatch(item, 1));
			dto.setSocket2Matching(equipment.isCompleteMatch(item, 2));
			dto.setSocket3Matching(equipment.isCompleteMatch(item, 3));
			dto.setSocketBonusEnabled(equipment.allGemsMatch(item));
		}
		return dto;
	}
}

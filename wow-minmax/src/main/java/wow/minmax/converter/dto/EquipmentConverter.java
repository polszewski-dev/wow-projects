package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.Equipment;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.EquipmentDTO;

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
						Map.Entry::getKey, e -> equippableItemConverter.convert(e.getValue()))
				);

		return new EquipmentDTO(itemsBySlot);
	}
}

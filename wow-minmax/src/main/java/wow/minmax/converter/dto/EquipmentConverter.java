package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.Equipment;
import wow.minmax.converter.ParametrizedConverter;
import wow.minmax.model.dto.EquipmentDTO;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class EquipmentConverter extends ParametrizedConverter<Equipment, EquipmentDTO> {
	private final EquippableItemConverter equippableItemConverter;

	@Override
	protected EquipmentDTO doConvert(Equipment equipment, Map<String, Object> params) {
		var itemsBySlot = equipment.toMap().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey, e -> equippableItemConverter.convert(e.getValue(), params))
				);

		return new EquipmentDTO(itemsBySlot);
	}
}

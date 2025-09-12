package wow.commons.client.converter.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.Equipment;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.client.dto.equipment.EquipmentDTO;
import wow.commons.model.pve.PhaseId;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class EquipmentConverter implements Converter<Equipment, EquipmentDTO>, ParametrizedBackConverter<Equipment, EquipmentDTO, PhaseId> {
	private final EquippableItemConverter equippableItemConverter;

	@Override
	public EquipmentDTO doConvert(Equipment source) {
		var itemsBySlot = source.toMap().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey, e -> equippableItemConverter.convert(e.getValue()))
				);

		return new EquipmentDTO(itemsBySlot);
	}

	@Override
	public Equipment doConvertBack(EquipmentDTO source, PhaseId phaseId) {
		var itemsBySlot = source.itemsBySlot().entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey, e -> equippableItemConverter.convertBack(e.getValue(), phaseId))
				);

		var equipment = new Equipment();

		for (var entry : itemsBySlot.entrySet()) {
			equipment.equip(entry.getValue(), entry.getKey());
		}

		return equipment;
	}
}

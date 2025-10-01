package wow.minmax.converter.dto.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.Equipment;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.pve.PhaseId;
import wow.minmax.client.dto.equipment.EquipmentDTO;

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
		var itemsBySlot = equippableItemConverter.convertMap(source.toMap());

		return new EquipmentDTO(itemsBySlot);
	}

	@Override
	public Equipment doConvertBack(EquipmentDTO source, PhaseId phaseId) {
		var itemsBySlot = equippableItemConverter.convertBackMap(source.itemsBySlot(), phaseId);
		var equipment = new Equipment();

		for (var entry : itemsBySlot.entrySet()) {
			equipment.equip(entry.getValue(), entry.getKey());
		}

		return equipment;
	}
}

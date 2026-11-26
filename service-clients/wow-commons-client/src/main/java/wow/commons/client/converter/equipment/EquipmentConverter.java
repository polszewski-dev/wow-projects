package wow.commons.client.converter.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.Character;
import wow.character.model.equipment.Equipment;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.client.dto.equipment.EquipmentDTO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component("commonsEquipmentConverter")
@AllArgsConstructor
public class EquipmentConverter implements Converter<Equipment, EquipmentDTO>, ParametrizedBackConverter<Void, EquipmentDTO, Character> {
	private final EquippableItemConverter equippableItemConverter;

	@Override
	public EquipmentDTO doConvert(Equipment source) {
		var itemsBySlot = equippableItemConverter.convertMap(source.toMap());

		return new EquipmentDTO(itemsBySlot);
	}

	@Override
	public Void doConvertBack(EquipmentDTO source, Character character) {
		var itemsBySlot = equippableItemConverter.convertBackMap(source.itemsBySlot(), character.getPhaseId());

		for (var entry : itemsBySlot.entrySet()) {
			character.equip(entry.getValue(), entry.getKey());
		}

		return null;
	}
}

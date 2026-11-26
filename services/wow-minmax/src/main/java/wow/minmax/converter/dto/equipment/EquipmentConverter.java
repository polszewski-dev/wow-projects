package wow.minmax.converter.dto.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.Equipment;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.minmax.client.dto.equipment.EquipmentDTO;
import wow.minmax.model.Player;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class EquipmentConverter implements Converter<Equipment, EquipmentDTO>, ParametrizedBackConverter<Void, EquipmentDTO, Player> {
	private final EquippableItemConverter equippableItemConverter;

	@Override
	public EquipmentDTO doConvert(Equipment source) {
		var itemsBySlot = equippableItemConverter.convertMap(source.toMap());

		return new EquipmentDTO(itemsBySlot);
	}

	@Override
	public Void doConvertBack(EquipmentDTO source, Player player) {
		var itemsBySlot = equippableItemConverter.convertBackMap(source.itemsBySlot(), player.getPhaseId());

		for (var entry : itemsBySlot.entrySet()) {
			player.equip(entry.getValue(), entry.getKey());
		}

		return null;
	}
}

package wow.minmax.converter.dto.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.equipment.EquipmentSocketStatusDTO;
import wow.minmax.model.equipment.EquipmentSocketStatus;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Component
@AllArgsConstructor
public class EquipmentSocketStatusConverter implements Converter<EquipmentSocketStatus, EquipmentSocketStatusDTO> {
	private final ItemSocketStatusConverter itemSocketStatusConverter;

	@Override
	public EquipmentSocketStatusDTO doConvert(EquipmentSocketStatus source) {
		var socketStatusesByItemSlot = itemSocketStatusConverter.convertMap(source.socketStatusesByItemSlot());

		return new EquipmentSocketStatusDTO(socketStatusesByItemSlot);
	}
}

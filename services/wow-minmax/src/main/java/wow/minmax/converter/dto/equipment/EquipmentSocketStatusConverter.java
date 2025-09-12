package wow.minmax.converter.dto.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.equipment.EquipmentSocketStatusDTO;
import wow.minmax.model.equipment.EquipmentSocketStatus;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

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
		var socketStatusesByItemSlot = source.socketStatusesByItemSlot().entrySet().stream()
				.collect(
						toMap(
								Map.Entry::getKey,
								e -> itemSocketStatusConverter.convert(e.getValue())
						)
				);

		return new EquipmentSocketStatusDTO(socketStatusesByItemSlot);
	}
}

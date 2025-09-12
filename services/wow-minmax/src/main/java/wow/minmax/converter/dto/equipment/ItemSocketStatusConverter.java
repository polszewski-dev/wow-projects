package wow.minmax.converter.dto.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.equipment.ItemSocketStatusDTO;
import wow.minmax.model.equipment.ItemSocketStatus;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Component
@AllArgsConstructor
public class ItemSocketStatusConverter implements Converter<ItemSocketStatus, ItemSocketStatusDTO> {
	private final SocketStatusConverter socketStatusConverter;
	private final SocketBonusStatusConverter socketBonusStatusConverter;

	@Override
	public ItemSocketStatusDTO doConvert(ItemSocketStatus source) {
		return new ItemSocketStatusDTO(
				socketStatusConverter.convertList(source.socketStatuses()),
				socketBonusStatusConverter.convert(source.socketBonusStatus())
		);
	}
}

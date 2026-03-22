package wow.minmax.converter.dto;

import org.springframework.stereotype.Component;
import wow.commons.model.item.Consumable;
import wow.minmax.client.dto.ConsumableDTO;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Component
public class ConsumableStatusConverter extends OptionStatusConverter<Consumable, ConsumableDTO> {
	public ConsumableStatusConverter(ConsumableConverter converter) {
		super(converter);
	}

	@Override
	protected String getGroupKey(Consumable source) {
		return source.getName();
	}
}

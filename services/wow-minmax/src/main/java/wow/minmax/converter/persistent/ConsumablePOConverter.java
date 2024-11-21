package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.item.Consumable;
import wow.minmax.model.persistent.ConsumablePO;

/**
 * User: POlszewski
 * Date: 2024-11-21
 */
@Component
@AllArgsConstructor
public class ConsumablePOConverter implements Converter<Consumable, ConsumablePO> {
	@Override
	public ConsumablePO doConvert(Consumable source) {
		return new ConsumablePO(
				source.getId(),
				source.getName()
		);
	}
}

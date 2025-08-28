package wow.minmax.converter.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.item.Consumable;
import wow.minmax.model.ConsumableConfig;

/**
 * User: POlszewski
 * Date: 2024-11-21
 */
@Component
@AllArgsConstructor
public class ConsumableConfigConverter implements Converter<Consumable, ConsumableConfig> {
	@Override
	public ConsumableConfig doConvert(Consumable source) {
		return new ConsumableConfig(
				source.getId(),
				source.getName()
		);
	}
}

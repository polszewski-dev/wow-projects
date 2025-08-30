package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.dto.ConsumableDTO;
import wow.commons.model.item.Consumable;

/**
 * User: POlszewski
 * Date: 2024-11-22
 */
@Component
@AllArgsConstructor
public class ConsumableConverter implements Converter<Consumable, ConsumableDTO> {
	@Override
	public ConsumableDTO doConvert(Consumable source) {
		return new ConsumableDTO(
				source.getId(),
				source.getName(),
				source.getTooltip(),
				source.getIcon(),
				source.getTooltip()
		);
	}
}

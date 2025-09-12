package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.item.Consumable;
import wow.minmax.client.dto.ConsumableDTO;

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

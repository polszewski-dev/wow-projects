package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.ConsumableStatusDTO;
import wow.minmax.model.ConsumableStatus;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Component
@AllArgsConstructor
public class ConsumableStatusConverter implements Converter<ConsumableStatus, ConsumableStatusDTO> {
	private final ConsumableConverter consumableConverter;

	@Override
	public ConsumableStatusDTO doConvert(ConsumableStatus source) {
		return new ConsumableStatusDTO(
				consumableConverter.convert(source.consumable()),
				source.enabled()
		);
	}
}

package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.effect.RacialEffect;
import wow.minmax.client.dto.RacialDTO;

/**
 * User: POlszewski
 * Date: 2023-04-04
 */
@Component
@AllArgsConstructor
public class RacialConverter implements Converter<RacialEffect, RacialDTO> {
	@Override
	public RacialDTO doConvert(RacialEffect source) {
		return new RacialDTO(
				source.getName(),
				source.getTooltip(),
				source.getIcon(),
				source.getTooltip()
		);
	}
}

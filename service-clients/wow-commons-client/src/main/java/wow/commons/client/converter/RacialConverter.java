package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.dto.RacialDTO;
import wow.commons.model.effect.RacialEffect;

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

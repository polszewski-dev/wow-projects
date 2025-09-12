package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.buff.Buff;
import wow.minmax.client.dto.BuffDTO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class BuffConverter implements Converter<Buff, BuffDTO> {
	@Override
	public BuffDTO doConvert(Buff source) {
		return new BuffDTO(
				source.getId().value(),
				source.getName(),
				source.getRank(),
				source.getEffect().getTooltip(),
				source.getIcon(),
				source.getTooltip()
		);
	}
}

package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.buff.Buff;
import wow.commons.client.dto.BuffDTO;

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
				source.getBuffId(),
				source.getRank(),
				source.getName(),
				source.getEffect().getTooltip(),
				source.getIcon(),
				source.getTooltip(),
				false
		);
	}
}

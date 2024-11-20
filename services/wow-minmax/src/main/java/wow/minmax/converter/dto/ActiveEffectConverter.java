package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.buff.Buff;
import wow.simulator.client.dto.ActiveEffectDTO;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@Component
@AllArgsConstructor
public class ActiveEffectConverter implements Converter<Buff, ActiveEffectDTO> {
	@Override
	public ActiveEffectDTO doConvert(Buff source) {
		return new ActiveEffectDTO(
				source.getEffect().getEffectId(),
				source.getStacks()
		);
	}
}

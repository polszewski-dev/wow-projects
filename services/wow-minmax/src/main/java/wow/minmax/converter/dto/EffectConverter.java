package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.effect.Effect;
import wow.minmax.client.dto.EffectDTO;

/**
 * User: POlszewski
 * Date: 2025-09-23
 */
@Component
@AllArgsConstructor
public class EffectConverter implements Converter<Effect, EffectDTO> {
	@Override
	public EffectDTO doConvert(Effect source) {
		return new EffectDTO(
				source.getId().value(),
				source.getName(),
				source.getIcon(),
				source.getTooltip()
		);
	}
}

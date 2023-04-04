package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.Racial;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.RacialDTO;

/**
 * User: POlszewski
 * Date: 2023-04-04
 */
@Component
@AllArgsConstructor
public class RacialConverter implements Converter<Racial, RacialDTO> {
	@Override
	public RacialDTO doConvert(Racial source) {
		return new RacialDTO(
				source.getName(),
				source.statString(),
				source.getIcon(),
				source.getTooltip()
		);
	}
}

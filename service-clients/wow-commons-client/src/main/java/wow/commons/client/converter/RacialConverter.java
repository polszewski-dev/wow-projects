package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.config.Described;
import wow.commons.model.racial.Racial;
import wow.commons.client.dto.RacialDTO;

import java.util.stream.Collectors;

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
				source.getEffects().stream().map(Described::getTooltip).collect(Collectors.joining("\n")),
				source.getIcon(),
				source.getTooltip()
		);
	}
}

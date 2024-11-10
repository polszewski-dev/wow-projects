package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.character.Race;
import wow.commons.client.dto.RaceDTO;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class RaceConverter implements Converter<Race, RaceDTO> {
	private final RacialConverter racialConverter;

	@Override
	public RaceDTO doConvert(Race source) {
		return new RaceDTO(
				source.getRaceId(),
				source.getName(),
				source.getIcon(),
				racialConverter.convertList(source.getRacials())
		);
	}
}

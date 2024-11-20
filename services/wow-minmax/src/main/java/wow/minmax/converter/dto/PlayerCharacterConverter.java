package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.CharacterClassConverter;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.RaceConverter;
import wow.commons.client.converter.RacialConverter;
import wow.commons.client.dto.RaceDTO;
import wow.minmax.client.dto.PlayerCharacterDTO;
import wow.minmax.model.PlayerCharacter;

/**
 * User: POlszewski
 * Date: 2023-04-09
 */
@Component
@AllArgsConstructor
public class PlayerCharacterConverter implements Converter<PlayerCharacter, PlayerCharacterDTO> {
	private final CharacterClassConverter characterClassConverter;
	private final RaceConverter raceConverter;
	private final RacialConverter racialConverter;

	@Override
	public PlayerCharacterDTO doConvert(PlayerCharacter source) {
		return new PlayerCharacterDTO(
				null,
				characterClassConverter.convert(source.getCharacterClass()),
				getRace(source)
		);
	}

	private RaceDTO getRace(PlayerCharacter source) {
		var racials = racialConverter.convertList(source.getRacials());

		return raceConverter
				.convert(source.getRace())
				.withRacials(racials);
	}
}

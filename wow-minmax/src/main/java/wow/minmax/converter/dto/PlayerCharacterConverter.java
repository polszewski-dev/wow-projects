package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.PlayerCharacter;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.PlayerCharacterDTO;
import wow.minmax.model.dto.RaceDTO;

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
		RaceDTO dto = raceConverter.convert(source.getRace());
		dto.setRacials(racialConverter.convertList(source.getRacials())); // replace with racials filtered by character
		return dto;
	}
}

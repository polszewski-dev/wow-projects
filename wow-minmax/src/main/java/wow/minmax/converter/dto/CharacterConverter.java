package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.Character;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.CharacterDTO;
import wow.minmax.model.dto.RaceDTO;

/**
 * User: POlszewski
 * Date: 2023-04-09
 */
@Component
@AllArgsConstructor
public class CharacterConverter implements Converter<Character, CharacterDTO> {
	private final CharacterClassConverter characterClassConverter;
	private final RaceConverter raceConverter;
	private final RacialConverter racialConverter;

	@Override
	public CharacterDTO doConvert(Character source) {
		return new CharacterDTO(
				null,
				characterClassConverter.convert(source.getCharacterClass()),
				getRace(source)
		);
	}

	private RaceDTO getRace(Character source) {
		RaceDTO dto = raceConverter.convert(source.getRace());
		dto.setRacials(racialConverter.convertList(source.getRacials())); // replace with racials filtered by character
		return dto;
	}
}

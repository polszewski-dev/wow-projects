package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.character.CharacterClass;
import wow.commons.client.dto.CharacterClassDTO;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class CharacterClassConverter implements Converter<CharacterClass, CharacterClassDTO> {
	private final RaceConverter raceConverter;

	@Override
	public CharacterClassDTO doConvert(CharacterClass source) {
		return new CharacterClassDTO(
				source.getCharacterClassId(),
				source.getName(),
				source.getIcon(),
				raceConverter.convertList(source.getRaces()),
				source.getSpellSchools()
		);
	}
}

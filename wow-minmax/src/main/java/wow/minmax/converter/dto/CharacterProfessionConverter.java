package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.minmax.converter.BackConverter;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.CharacterProfessionDTO;

/**
 * User: POlszewski
 * Date: 2023-01-07
 */
@Component
@AllArgsConstructor
public class CharacterProfessionConverter implements Converter<CharacterProfession, CharacterProfessionDTO>, BackConverter<CharacterProfession, CharacterProfessionDTO> {
	@Override
	public CharacterProfessionDTO doConvert(CharacterProfession value) {
		return new CharacterProfessionDTO(
				value.getProfession(),
				value.getSpecialization()
		);
	}

	@Override
	public CharacterProfession doConvertBack(CharacterProfessionDTO value) {
		return new CharacterProfession(
				value.getProfession(),
				value.getSpecialization()
		);
	}
}

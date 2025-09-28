package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.ProfIdSpecIdLevel;
import wow.commons.client.dto.CharacterProfessionDTO;

/**
 * User: POlszewski
 * Date: 2023-01-07
 */
@Component("commonsCharacterProfessionConverter")
@AllArgsConstructor
public class CharacterProfessionConverter implements Converter<CharacterProfession, CharacterProfessionDTO>, BackConverter<ProfIdSpecIdLevel, CharacterProfessionDTO> {
	@Override
	public CharacterProfessionDTO doConvert(CharacterProfession source) {
		return new CharacterProfessionDTO(
				source.professionId(),
				source.specializationId(),
				source.level()
		);
	}

	@Override
	public ProfIdSpecIdLevel doConvertBack(CharacterProfessionDTO source) {
		return new ProfIdSpecIdLevel(
				source.profession(),
				source.specialization(),
				source.level()
		);
	}
}

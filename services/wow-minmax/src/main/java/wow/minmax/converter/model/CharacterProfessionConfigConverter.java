package wow.minmax.converter.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.ProfIdSpecIdLevel;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.minmax.model.CharacterProfessionConfig;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@Component
@AllArgsConstructor
public class CharacterProfessionConfigConverter implements Converter<CharacterProfession, CharacterProfessionConfig>, BackConverter<ProfIdSpecIdLevel, CharacterProfessionConfig> {
	@Override
	public CharacterProfessionConfig doConvert(CharacterProfession source) {
		return new CharacterProfessionConfig(
				source.professionId(), source.specializationId(), source.level()
		);
	}

	@Override
	public ProfIdSpecIdLevel doConvertBack(CharacterProfessionConfig source) {
		return new ProfIdSpecIdLevel(
				source.getProfessionId(),
				source.getSpecializationId(),
				source.getLevel()
		);
	}
}

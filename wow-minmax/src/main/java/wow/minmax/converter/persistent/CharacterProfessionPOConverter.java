package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.minmax.converter.BackConverter;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.CharacterProfessionPO;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@Component
@AllArgsConstructor
public class CharacterProfessionPOConverter implements Converter<CharacterProfession, CharacterProfessionPO>, BackConverter<CharacterProfession, CharacterProfessionPO> {
	@Override
	public CharacterProfessionPO doConvert(CharacterProfession characterProfession) {
		return new CharacterProfessionPO(characterProfession.getProfessionId(), characterProfession.getSpecializationId());
	}

	@Override
	public CharacterProfession doConvertBack(CharacterProfessionPO characterProfession) {
		return new CharacterProfession(characterProfession.getProfessionId(), characterProfession.getSpecializationId());
	}
}

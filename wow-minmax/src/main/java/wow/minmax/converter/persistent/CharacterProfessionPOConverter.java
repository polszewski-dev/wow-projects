package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.CharacterProfessionPO;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@Component
@AllArgsConstructor
public class CharacterProfessionPOConverter implements Converter<CharacterProfession, CharacterProfessionPO>, ParametrizedBackConverter<CharacterProfession, CharacterProfessionPO> {
	@Override
	public CharacterProfessionPO doConvert(CharacterProfession characterProfession) {
		return new CharacterProfessionPO(characterProfession.getProfession(), characterProfession.getLevel(), characterProfession.getSpecialization());
	}

	@Override
	public CharacterProfession doConvertBack(CharacterProfessionPO characterProfession, Map<String, Object> params) {
		return new CharacterProfession(characterProfession.getProfession(), characterProfession.getLevel(), characterProfession.getSpecialization());
	}
}

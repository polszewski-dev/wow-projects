package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.minmax.converter.ParametrizedConverter;
import wow.minmax.model.persistent.CharacterProfessionPO;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@Component
@AllArgsConstructor
public class CharacterProfessionPOConverter extends ParametrizedConverter<CharacterProfession, CharacterProfessionPO> {
	@Override
	protected CharacterProfessionPO doConvert(CharacterProfession characterProfession, Map<String, Object> params) {
		return new CharacterProfessionPO(characterProfession.getProfession(), characterProfession.getLevel(), characterProfession.getSpecialization());
	}

	@Override
	protected CharacterProfession doConvertBack(CharacterProfessionPO characterProfession, Map<String, Object> params) {
		return new CharacterProfession(characterProfession.getProfession(), characterProfession.getLevel(), characterProfession.getSpecialization());
	}
}

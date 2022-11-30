package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.character.CharacterProfession;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.CharacterProfessionPO;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@Component
@AllArgsConstructor
public class CharacterProfessionPOConverter extends Converter<CharacterProfession, CharacterProfessionPO> {
	@Override
	protected CharacterProfessionPO doConvert(CharacterProfession characterProfession) {
		return new CharacterProfessionPO(characterProfession.getProfession(), characterProfession.getLevel(), characterProfession.getSpecialization());
	}

	@Override
	protected CharacterProfession doConvertBack(CharacterProfessionPO characterProfession) {
		return new CharacterProfession(characterProfession.getProfession(), characterProfession.getLevel(), characterProfession.getSpecialization());
	}
}

package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.Phase;
import wow.character.repository.CharacterRepository;
import wow.commons.model.pve.PhaseId;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.CharacterProfessionPO;

import java.util.Map;

import static wow.minmax.converter.persistent.PoConverterParams.getPhaseId;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@Component
@AllArgsConstructor
public class CharacterProfessionPOConverter implements Converter<CharacterProfession, CharacterProfessionPO>, ParametrizedBackConverter<CharacterProfession, CharacterProfessionPO> {
	private final CharacterRepository characterRepository;

	@Override
	public CharacterProfessionPO doConvert(CharacterProfession source) {
		return new CharacterProfessionPO(
				source.getProfessionId(), source.getSpecializationId(), source.getLevel()
		);
	}

	@Override
	public CharacterProfession doConvertBack(CharacterProfessionPO source, Map<String, Object> params) {
		PhaseId phaseId = getPhaseId(params);
		Phase phase = characterRepository.getPhase(phaseId).orElseThrow();

		return phase.getCharacterProfession(
				source.getProfessionId(), source.getSpecializationId(), source.getLevel()
		);
	}
}

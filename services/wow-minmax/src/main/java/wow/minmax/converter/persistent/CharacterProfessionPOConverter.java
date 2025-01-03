package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.pve.PhaseRepository;
import wow.minmax.model.persistent.CharacterProfessionPO;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@Component
@AllArgsConstructor
public class CharacterProfessionPOConverter implements Converter<CharacterProfession, CharacterProfessionPO>, ParametrizedBackConverter<CharacterProfession, CharacterProfessionPO, PhaseId> {
	private final PhaseRepository phaseRepository;

	@Override
	public CharacterProfessionPO doConvert(CharacterProfession source) {
		return new CharacterProfessionPO(
				source.getProfessionId(), source.getSpecializationId(), source.getLevel()
		);
	}

	@Override
	public CharacterProfession doConvertBack(CharacterProfessionPO source, PhaseId phaseId) {
		Phase phase = phaseRepository.getPhase(phaseId).orElseThrow();

		return CharacterProfession.getCharacterProfession(
				phase, source.getProfessionId(), source.getSpecializationId(), source.getLevel()
		);
	}
}

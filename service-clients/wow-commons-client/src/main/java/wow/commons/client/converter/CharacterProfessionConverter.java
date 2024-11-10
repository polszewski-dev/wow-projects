package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.commons.client.dto.CharacterProfessionDTO;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.pve.PhaseRepository;

/**
 * User: POlszewski
 * Date: 2023-01-07
 */
@Component
@AllArgsConstructor
public class CharacterProfessionConverter implements Converter<CharacterProfession, CharacterProfessionDTO>, ParametrizedBackConverter<CharacterProfession, CharacterProfessionDTO, PhaseId> {
	private final PhaseRepository phaseRepository;

	@Override
	public CharacterProfessionDTO doConvert(CharacterProfession source) {
		return new CharacterProfessionDTO(
				source.getProfessionId(),
				source.getSpecializationId(),
				source.getLevel()
		);
	}

	@Override
	public CharacterProfession doConvertBack(CharacterProfessionDTO source, PhaseId phaseId) {
		Phase phase = phaseRepository.getPhase(phaseId).orElseThrow();

		return CharacterProfession.getCharacterProfession(
				phase, source.profession(), source.specialization(), source.level()
		);
	}
}

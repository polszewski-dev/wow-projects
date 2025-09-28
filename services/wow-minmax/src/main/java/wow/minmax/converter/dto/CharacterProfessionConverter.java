package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.pve.PhaseRepository;
import wow.minmax.client.dto.CharacterProfessionDTO;

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
				source.professionId(),
				source.specializationId(),
				source.level()
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

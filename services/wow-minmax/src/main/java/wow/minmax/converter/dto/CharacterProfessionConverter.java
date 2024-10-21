package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.pve.PhaseRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.dto.CharacterProfessionDTO;

import java.util.Map;

import static wow.minmax.converter.dto.DtoConverterParams.getPhaseId;

/**
 * User: POlszewski
 * Date: 2023-01-07
 */
@Component
@AllArgsConstructor
public class CharacterProfessionConverter implements Converter<CharacterProfession, CharacterProfessionDTO>, ParametrizedBackConverter<CharacterProfession, CharacterProfessionDTO> {
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
	public CharacterProfession doConvertBack(CharacterProfessionDTO source, Map<String, Object> params) {
		PhaseId phaseId = getPhaseId(params);
		Phase phase = phaseRepository.getPhase(phaseId).orElseThrow();

		return CharacterProfession.getCharacterProfession(
				phase, source.getProfession(), source.getSpecialization(), source.getLevel()
		);
	}
}

package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.GameVersion;
import wow.character.repository.CharacterRepository;
import wow.commons.model.pve.PhaseId;
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
	private final CharacterRepository characterRepository;

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
		GameVersion gameVersion = characterRepository.getPhase(phaseId).orElseThrow().getGameVersion();

		return gameVersion.getCharacterProfession(
				source.getProfession(), source.getSpecialization(), source.getLevel()
		);
	}
}

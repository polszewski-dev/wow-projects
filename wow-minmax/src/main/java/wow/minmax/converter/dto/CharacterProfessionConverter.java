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

import static wow.minmax.converter.dto.DtoConverterParams.getPhase;

/**
 * User: POlszewski
 * Date: 2023-01-07
 */
@Component
@AllArgsConstructor
public class CharacterProfessionConverter implements Converter<CharacterProfession, CharacterProfessionDTO>, ParametrizedBackConverter<CharacterProfession, CharacterProfessionDTO> {
	private final CharacterRepository characterRepository;

	@Override
	public CharacterProfessionDTO doConvert(CharacterProfession value) {
		return new CharacterProfessionDTO(
				value.getProfessionId(),
				value.getSpecializationId()
		);
	}

	@Override
	public CharacterProfession doConvertBack(CharacterProfessionDTO value, Map<String, Object> params) {
		PhaseId phaseId = getPhase(params);
		GameVersion gameVersion = characterRepository.getPhase(phaseId).orElseThrow().getGameVersion();

		return gameVersion.getCharacterProfession(
				value.getProfession(), value.getSpecialization()
		);
	}
}

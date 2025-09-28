package wow.minmax.converter.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.pve.PhaseRepository;
import wow.minmax.model.CharacterProfessionConfig;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@Component
@AllArgsConstructor
public class CharacterProfessionConfigConverter implements Converter<CharacterProfession, CharacterProfessionConfig>, ParametrizedBackConverter<CharacterProfession, CharacterProfessionConfig, PhaseId> {
	private final PhaseRepository phaseRepository;

	@Override
	public CharacterProfessionConfig doConvert(CharacterProfession source) {
		return new CharacterProfessionConfig(
				source.professionId(), source.specializationId(), source.level()
		);
	}

	@Override
	public CharacterProfession doConvertBack(CharacterProfessionConfig source, PhaseId phaseId) {
		Phase phase = phaseRepository.getPhase(phaseId).orElseThrow();

		return CharacterProfession.getCharacterProfession(
				phase, source.getProfessionId(), source.getSpecializationId(), source.getLevel()
		);
	}
}

package wow.commons.client.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.character.CharacterProfession;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.dto.CharacterProfessionDTO;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.character.ProfessionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.profession.ProfessionId.TAILORING;
import static wow.commons.model.profession.ProfessionSpecializationId.SHADOWEAVE_TAILORING;
import static wow.commons.model.pve.GameVersionId.TBC;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class CharacterProfessionConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	CharacterProfessionConverter characterProfessionConverter;

	@Autowired
	ProfessionRepository professionRepository;

	@Test
	void convert() {
		var profession = professionRepository.getProfession(TAILORING, TBC).orElseThrow();
		var specialization = profession.getSpecialization(SHADOWEAVE_TAILORING).orElseThrow();
		var charProfession = new CharacterProfession(profession, specialization, 375);

		var converted = characterProfessionConverter.convert(charProfession);

		assertThat(converted).isEqualTo(
				new CharacterProfessionDTO(
						TAILORING, SHADOWEAVE_TAILORING, 375
				)
		);
	}

	@Test
	void convertBack() {
		var profession = new CharacterProfessionDTO(TAILORING, SHADOWEAVE_TAILORING, 375);
		var converted = characterProfessionConverter.convertBack(profession, PhaseId.TBC_P5);

		assertThat(converted.professionId()).isEqualTo(TAILORING);
		assertThat(converted.specializationId()).isEqualTo(SHADOWEAVE_TAILORING);
		assertThat(converted.level()).isEqualTo(375);
	}
}
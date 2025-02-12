package wow.commons.client.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.dto.TalentDTO;
import wow.commons.repository.spell.TalentRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.commons.model.talent.TalentId.IMPROVED_SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class TalentConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	TalentConverter talentConverter;

	@Autowired
	TalentRepository talentRepository;

	@Test
	void convert() {
		var talent = talentRepository.getTalent(WARLOCK, IMPROVED_SHADOW_BOLT, 1, TBC_P5).orElseThrow();
		var converted = talentConverter.convert(talent);

		assertThat(converted).isEqualTo(
				new TalentDTO(
						IMPROVED_SHADOW_BOLT,
						"Improved Shadow Bolt",
						1,
						5,
						"spell_shadow_shadowbolt",
						"Your Shadow Bolt critical strikes increase Shadow damage dealt to the target by 4% until 4 non-periodic damage sources are applied. Effect lasts a maximum of 12 sec."
				)
		);
	}
}
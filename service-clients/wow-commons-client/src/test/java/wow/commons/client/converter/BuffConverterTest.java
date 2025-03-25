package wow.commons.client.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.dto.BuffDTO;
import wow.commons.model.buff.BuffId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.spell.BuffRepository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class BuffConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	BuffConverter buffConverter;

	@Autowired
	BuffRepository buffRepository;

	@Test
	void convert() {
		var buff = buffRepository.getBuff(BuffId.FEL_ARMOR, 2, PhaseId.TBC_P5).getFirst();

		var converted = buffConverter.convert(buff);

		assertThat(converted).isEqualTo(
				new BuffDTO(
						BuffId.FEL_ARMOR,
						2,
						"Fel Armor",
						"Surrounds the caster with fel energy, increasing the amount of health generated through spells and effects by 20% and increasing spell damage by up to 100.  Only one type of Armor spell can be active on the Warlock at any time.  Lasts 30 min.",
						"spell_shadow_felarmour",
						"Surrounds the caster with fel energy, increasing the amount of health generated through spells and effects by 20% and increasing spell damage by up to 100.  Only one type of Armor spell can be active on the Warlock at any time.  Lasts 30 min.",
						false
				)
		);
	}
}
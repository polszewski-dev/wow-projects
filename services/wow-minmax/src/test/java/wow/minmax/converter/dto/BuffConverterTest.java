package wow.minmax.converter.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.repository.spell.BuffRepository;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.client.dto.BuffDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.test.commons.BuffNames.FEL_ARMOR;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class BuffConverterTest extends WowMinMaxSpringTest {
	@Autowired
	BuffConverter buffConverter;

	@Autowired
	BuffRepository buffRepository;

	@Test
	void convert() {
		var buff = buffRepository.getBuff(FEL_ARMOR, 2, TBC_P5).getFirst();

		var converted = buffConverter.convert(buff);

		assertThat(converted).isEqualTo(
				new BuffDTO(
						28189,
						FEL_ARMOR,
						2,
						"Surrounds the caster with fel energy, increasing the amount of health generated through spells and effects by 20% and increasing spell damage by up to 100. Only one type of Armor spell can be active on the Warlock at any time. Lasts 30 min.",
						"spell_shadow_felarmour",
						"Surrounds the caster with fel energy, increasing the amount of health generated through spells and effects by 20% and increasing spell damage by up to 100. Only one type of Armor spell can be active on the Warlock at any time. Lasts 30 min."
				)
		);
	}
}
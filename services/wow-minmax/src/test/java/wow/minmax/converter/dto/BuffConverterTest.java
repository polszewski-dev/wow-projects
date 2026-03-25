package wow.minmax.converter.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.repository.spell.BuffRepository;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.client.dto.BuffDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.test.commons.BuffNames.FLASK_OF_BLINDING_LIGHT;

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
		var buff = buffRepository.getBuff(FLASK_OF_BLINDING_LIGHT, TBC_P5).getFirst();

		var converted = buffConverter.convert(buff);

		assertThat(converted).isEqualTo(
				new BuffDTO(
						28521,
						FLASK_OF_BLINDING_LIGHT,
						"Increases spell damage caused by Arcane, Holy and Nature spells by up to 80 for 2 hrs.  Counts as both a Battle and Guardian elixir.  This effect persists through death.",
						"inv_potion_116",
						"Increases spell damage caused by Arcane, Holy and Nature spells by up to 80 for 2 hrs.  Counts as both a Battle and Guardian elixir.  This effect persists through death."
				)
		);
	}
}
package wow.minmax.converter.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.spell.SpellRepository;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.client.dto.AbilityDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class AbilityConverterTest extends WowMinMaxSpringTest {
	@Autowired
	AbilityConverter abilityConverter;

	@Autowired
	SpellRepository spellRepository;

	@Test
	void convert() {
		var ability = spellRepository.getAbility(SHADOW_BOLT, 1, PhaseId.TBC_P5).orElseThrow();

		var converted = abilityConverter.convert(ability);

		assertThat(converted).isEqualTo(new AbilityDTO(
				686,
				"Shadow Bolt",
				1,
				"spell_shadow_shadowbolt",
				"Sends a shadowy bolt at the enemy, causing 13 to 18 Shadow damage."
		));
	}
}
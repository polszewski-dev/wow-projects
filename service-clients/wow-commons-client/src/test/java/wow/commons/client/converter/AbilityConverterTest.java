package wow.commons.client.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.dto.AbilityDTO;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.AbilityId;
import wow.commons.repository.spell.SpellRepository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class AbilityConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	AbilityConverter abilityConverter;

	@Autowired
	SpellRepository spellRepository;

	@Test
	void convert() {
		var ability = spellRepository.getAbility(AbilityId.SHADOW_BOLT, 1, PhaseId.TBC_P5).orElseThrow();

		var converted = abilityConverter.convert(ability);

		assertThat(converted).isEqualTo(new AbilityDTO(
			"Shadow Bolt",
			1,
			"spell_shadow_shadowbolt",
			"Sends a shadowy bolt at the enemy, causing 13 to 18 Shadow damage."
		));
	}
}
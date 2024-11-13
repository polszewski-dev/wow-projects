package wow.minmax.client.dto;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.SpellSchool;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class CharacterStatsDTOTest {
	@Test
	void withType() {
		var dto = new CharacterStatsDTO(null, 1, Map.of(SpellSchool.FIRE, 10), 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
		var changed = dto.withType("Type");

		assertThat(changed).isEqualTo(new CharacterStatsDTO(
				"Type", 1, Map.of(SpellSchool.FIRE, 10), 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
		));
	}
}
package wow.commons.client.dto;

import org.junit.jupiter.api.Test;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.model.character.RaceId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class RaceDTOTest extends WowCommonsClientSpringTest {
	@Test
	void withRacials() {
		var race = new RaceDTO(RaceId.ORC, "name", "icon", List.of());
		var racial = new RacialDTO("racial", "attributes", "icon", "tooltip");
		var changed = race.withRacials(List.of(racial));

		assertThat(changed).isEqualTo(new RaceDTO(
				RaceId.ORC, "name", "icon", List.of(racial)
		));
	}
}
package wow.minmax.client.dto;

import org.junit.jupiter.api.Test;
import wow.commons.model.character.RaceId;
import wow.minmax.client.WowMinmaxClientSpringTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class RaceDTOTest extends WowMinmaxClientSpringTest {
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
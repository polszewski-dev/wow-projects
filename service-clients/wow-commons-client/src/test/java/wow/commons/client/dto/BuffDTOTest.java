package wow.commons.client.dto;

import org.junit.jupiter.api.Test;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.model.buff.BuffId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class BuffDTOTest extends WowCommonsClientSpringTest {
	@Test
	void withEnabled() {
		var dto = new BuffDTO(BuffId.FEL_ARMOR, 2, "name", "attributes", "icon", "tooltip", true);
		var changed = dto.withEnabled(false);

		assertThat(changed).isEqualTo(new BuffDTO(
				BuffId.FEL_ARMOR, 2, "name", "attributes", "icon", "tooltip", false
		));
	}
}
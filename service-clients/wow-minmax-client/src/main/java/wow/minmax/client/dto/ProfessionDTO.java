package wow.minmax.client.dto;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-09-26
 */
public record ProfessionDTO(
		String id,
		String name,
		String icon,
		String tooltip,
		String specializationId
) {
	public ProfessionDTO {
		Objects.requireNonNull(id);
	}
}

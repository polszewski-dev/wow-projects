package wow.minmax.client.dto;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-09-26
 */
public record ScriptInfoDTO(
		String id,
		String name
) {
	public ScriptInfoDTO {
		Objects.requireNonNull(id);
	}
}

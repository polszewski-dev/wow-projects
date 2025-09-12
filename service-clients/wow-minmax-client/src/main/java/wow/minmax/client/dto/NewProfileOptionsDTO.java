package wow.minmax.client.dto;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
public record NewProfileOptionsDTO(
		List<CharacterClassDTO> classOptions
) {
}

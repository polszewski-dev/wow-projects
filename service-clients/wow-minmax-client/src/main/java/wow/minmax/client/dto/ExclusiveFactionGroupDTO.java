package wow.minmax.client.dto;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-09-29
 */
public record ExclusiveFactionGroupDTO(
		String groupId,
		ExclusiveFactionDTO selectedFaction,
		List<ExclusiveFactionDTO> availableFactions
) {
}

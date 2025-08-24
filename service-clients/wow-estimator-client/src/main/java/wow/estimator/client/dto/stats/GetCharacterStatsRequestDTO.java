package wow.estimator.client.dto.stats;

import wow.commons.client.dto.PlayerDTO;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record GetCharacterStatsRequestDTO(
		PlayerDTO player,
		boolean worldBuffsAllowed
) {
}

package wow.evaluator.client.dto.stats;

import wow.commons.client.dto.PlayerDTO;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record GetTalentStatsRequestDTO(
		PlayerDTO player
) {
}

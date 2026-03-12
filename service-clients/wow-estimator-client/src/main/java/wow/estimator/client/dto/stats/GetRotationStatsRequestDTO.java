package wow.estimator.client.dto.stats;

import wow.commons.client.dto.NonPlayerDTO;
import wow.commons.client.dto.RaidDTO;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record GetRotationStatsRequestDTO(
		RaidDTO raid,
		NonPlayerDTO target
) {
}

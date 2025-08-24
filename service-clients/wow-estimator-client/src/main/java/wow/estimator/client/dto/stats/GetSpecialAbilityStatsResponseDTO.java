package wow.estimator.client.dto.stats;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record GetSpecialAbilityStatsResponseDTO(
		List<SpecialAbilityStatsDTO> stats
) {
}

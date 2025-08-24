package wow.estimator.client.dto.stats;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
public record RotationStatsDTO(
		List<RotationSpellStatsDTO> statList,
		double dps,
		double totalDamage
) {
}

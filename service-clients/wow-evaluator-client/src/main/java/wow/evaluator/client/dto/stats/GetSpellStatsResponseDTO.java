package wow.evaluator.client.dto.stats;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record GetSpellStatsResponseDTO(
		List<SpellStatsDTO> stats
) {
}

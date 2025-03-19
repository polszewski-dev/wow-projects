package wow.evaluator.client.dto.stats;

import wow.commons.client.dto.PlayerDTO;
import wow.commons.model.spell.AbilityId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record GetSpellStatsRequestDTO(
		PlayerDTO player,
		List<AbilityId> spells,
		boolean usesCombatRatings,
		double equivalentAmount
) {
}

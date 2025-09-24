package wow.estimator.client.dto.stats;

import wow.commons.client.dto.PlayerDTO;
import wow.commons.model.spell.AbilityId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record GetAbilityStatsRequestDTO(
		PlayerDTO player,
		List<AbilityId> abilityIds,
		boolean usesCombatRatings,
		double equivalentAmount
) {
}

package wow.estimator.client.dto.stats;

import wow.commons.client.dto.NonPlayerDTO;
import wow.commons.client.dto.RaidDTO;
import wow.commons.model.spell.AbilityId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record GetAbilityStatsRequestDTO(
		RaidDTO raid,
		NonPlayerDTO target,
		List<AbilityId> abilityIds,
		boolean usesCombatRatings,
		double equivalentAmount
) {
}

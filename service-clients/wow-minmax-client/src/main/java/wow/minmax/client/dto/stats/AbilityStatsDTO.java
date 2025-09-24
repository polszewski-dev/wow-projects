package wow.minmax.client.dto.stats;

import wow.minmax.client.dto.AbilityDTO;

/**
 * User: POlszewski
 * Date: 2025-09-03
 */
public record AbilityStatsDTO(
		AbilityDTO ability,
		double totalDamage,
		double dps,
		double castTime,
		boolean instantCast,
		double manaCost,
		double dpm,
		double sp,
		double totalHit,
		double totalCrit,
		double totalHaste,
		double spellCoeffDirect,
		double spellCoeffDoT,
		double critCoeff,
		double hitSpEqv,
		double critSpEqv,
		double hasteSpEqv,
		double duration,
		double cooldown,
		double threatPct,
		double pushbackPct
) {
}

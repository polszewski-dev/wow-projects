package wow.estimator.client.dto.stats;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public record AbilityStatsDTO(
		int spellId,
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

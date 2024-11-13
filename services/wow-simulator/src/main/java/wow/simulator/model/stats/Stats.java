package wow.simulator.model.stats;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.AbilityIdAndRank;
import wow.simulator.model.time.Time;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-08-20
 */
public class Stats {
	private final Map<AbilityIdAndRank, AbilityStats> abilityStatsById = new LinkedHashMap<>();
	private final Map<AbilityId, EffectStats> effectStatsById = new LinkedHashMap<>();
	private final Map<AbilityId, CooldownStats> cooldownStatsById = new LinkedHashMap<>();

	@Getter
	private int totalDamage;

	@Setter
	private Time simulationStart;

	@Setter
	private Time simulationEnd;

	public void addCastTime(Ability ability, Duration duration, boolean castFinished) {
		var abilityStats = getAbilityStats(ability);

		abilityStats.addTotalCastTime(duration);

		if (castFinished) {
			abilityStats.increaseNumCasts();
		}
	}

	public void addCooldownUptime(AbilityId abilityId, Duration duration) {
		getCooldownStats(abilityId).addUptime(duration);
	}

	public void addEffectUptime(AbilityId abilityId, Duration duration) {
		getEffectStats(abilityId).addUptime(duration);
	}

	public void addDamage(Ability ability, int damage, boolean crit) {
		getAbilityStats(ability).increaseTotalDamage(damage, crit);
		this.totalDamage += damage;
	}

	private AbilityStats getAbilityStats(Ability ability) {
		return abilityStatsById.computeIfAbsent(ability.getRankedAbilityId(), x -> new AbilityStats(ability));
	}

	private EffectStats getEffectStats(AbilityId abilityId) {
		return effectStatsById.computeIfAbsent(abilityId, x -> new EffectStats(abilityId));
	}

	private CooldownStats getCooldownStats(AbilityId abilityId) {
		return cooldownStatsById.computeIfAbsent(abilityId, x -> new CooldownStats(abilityId));
	}

	public List<AbilityStats> getAbilityStats() {
		return abilityStatsById.values().stream().toList();
	}

	public int getDps() {
		var simulationDuration = getSimulationDuration();

		if (simulationDuration.isZero()) {
			return 0;
		}
		return (int)(totalDamage / simulationDuration.getSeconds());
	}

	public Duration getSimulationDuration() {
		return simulationEnd.subtract(simulationStart);
	}
}

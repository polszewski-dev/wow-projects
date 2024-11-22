package wow.simulator.model.stats;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityIdAndRank;
import wow.commons.model.spell.CooldownId;
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
	private final Map<Integer, EffectStats> effectStatsById = new LinkedHashMap<>();
	private final Map<CooldownId, CooldownStats> cooldownStatsById = new LinkedHashMap<>();

	@Getter
	private int totalDamage;

	@Getter
	private int numCasts;

	@Setter
	private Time simulationStart;

	@Setter
	private Time simulationEnd;

	public void addCastTime(Ability ability, Duration duration, boolean castFinished) {
		var abilityStats = getAbilityStats(ability);

		abilityStats.addTotalCastTime(duration);

		if (castFinished) {
			abilityStats.increaseNumCasts();
			++numCasts;
		}
	}

	public void increaseNumHit(Ability ability) {
		getAbilityStats(ability).increaseNumHit();
	}

	public void addCooldownUptime(CooldownId cooldownId, Duration duration) {
		getCooldownStats(cooldownId).addUptime(duration);
	}

	public void addEffectUptime(int effectId, Duration duration) {
		getEffectStats(effectId).addUptime(duration);
	}

	public void addDamage(Ability ability, int damage, boolean crit) {
		getAbilityStats(ability).increaseTotalDamage(damage, crit);
		this.totalDamage += damage;
	}

	private AbilityStats getAbilityStats(Ability ability) {
		return abilityStatsById.computeIfAbsent(ability.getRankedAbilityId(), x -> new AbilityStats(ability));
	}

	private EffectStats getEffectStats(int effectId) {
		return effectStatsById.computeIfAbsent(effectId, EffectStats::new);
	}

	private CooldownStats getCooldownStats(CooldownId cooldownId) {
		return cooldownStatsById.computeIfAbsent(cooldownId, CooldownStats::new);
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

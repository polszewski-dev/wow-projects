package wow.simulator.model.stats;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectId;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityNameRank;
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
	private final Map<AbilityNameRank, AbilityStats> abilityStatsByNameRank = new LinkedHashMap<>();
	private final Map<EffectId, EffectStats> effectStatsById = new LinkedHashMap<>();
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

	public void addEffectUptime(Effect effect, Duration duration) {
		getEffectStats(effect).addUptime(duration);
	}

	public void addDamage(Ability ability, int damage, boolean crit) {
		getAbilityStats(ability).increaseTotalDamage(damage, crit);
		this.totalDamage += damage;
	}

	private AbilityStats getAbilityStats(Ability ability) {
		return abilityStatsByNameRank.computeIfAbsent(ability.getNameRank(), x -> new AbilityStats(ability));
	}

	private EffectStats getEffectStats(Effect effect) {
		return effectStatsById.computeIfAbsent(effect.getId(), x -> new EffectStats(effect));
	}

	private CooldownStats getCooldownStats(CooldownId cooldownId) {
		return cooldownStatsById.computeIfAbsent(cooldownId, CooldownStats::new);
	}

	public List<AbilityStats> getAbilityStats() {
		return List.copyOf(abilityStatsByNameRank.values());
	}

	public List<EffectStats> getEffectStats() {
		return List.copyOf(effectStatsById.values());
	}

	public List<CooldownStats> getCooldownStats() {
		return List.copyOf(cooldownStatsById.values());
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

package wow.simulator.graph;

import wow.commons.model.Duration;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.time.Time;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-08-20
 */
public class Statistics {
	private static class Statistic {
		private Duration effectUptime = Duration.ZERO;
		private Duration cdUptime = Duration.ZERO;
		private Duration totalCastTime = Duration.ZERO;
		private int totalDamage;
		private int numCasts;
	}

	private final Map<AbilityId, Statistic> statistics = new LinkedHashMap<>();
	private Time simulationStart;
	private Time simulationEnd;
	private int totalDamage = 0;

	public void addCastTime(AbilityId abilityId, Duration duration, boolean castFinished) {
		Statistic statistic = getStatistic(abilityId);
		statistic.totalCastTime = statistic.totalCastTime.add(duration);
		if (castFinished) {
			++statistic.numCasts;
		}
	}

	public void addCooldownUptime(AbilityId abilityId, Duration duration) {
		Statistic statistic = getStatistic(abilityId);
		statistic.cdUptime = statistic.cdUptime.add(duration);
	}

	public void addEffectUptime(AbilityId abilityId, Duration duration) {
		Statistic statistic = getStatistic(abilityId);
		statistic.effectUptime = statistic.effectUptime.add(duration);
	}

	public void addDamage(AbilityId abilityId, int damage) {
		getStatistic(abilityId).totalDamage += damage;
		this.totalDamage += damage;
	}

	public List<AbilityId> getEffects() {
		return statistics.entrySet().stream()
				.filter(x -> !x.getValue().effectUptime.isZero())
				.map(Map.Entry::getKey)
				.sorted(Comparator.comparing(this::getEffectUptimePct).reversed())
				.toList();
	}

	public List<AbilityId> getCooldowns() {
		return statistics.entrySet().stream()
				.filter(x -> !x.getValue().cdUptime.isZero())
				.map(Map.Entry::getKey)
				.sorted(Comparator.comparing(this::getCooldownUptimePct).reversed())
				.toList();
	}

	public List<AbilityId> getDamageSpells() {
		return statistics.entrySet().stream()
				.filter(x -> x.getValue().totalDamage != 0)
				.map(Map.Entry::getKey)
				.sorted(Comparator.comparing(this::getDps).reversed())
				.toList();
	}

	public double getEffectUptimePct(AbilityId abilityId) {
		return getDurationPct(getStatistic(abilityId).effectUptime);
	}

	public double getCooldownUptimePct(AbilityId abilityId) {
		return getDurationPct(getStatistic(abilityId).cdUptime);
	}

	public int getTotalDamage(AbilityId abilityId) {
		return getStatistic(abilityId).totalDamage;
	}

	public double getTotalDamagePct(AbilityId abilityId) {
		return 100.0 * getStatistic(abilityId).totalDamage / totalDamage;
	}

	public int getTotalDamage() {
		return totalDamage;
	}

	public int getDps(AbilityId abilityId) {
		Statistic statistic = getStatistic(abilityId);
		return (int)(statistic.totalDamage / statistic.totalCastTime.getSeconds());
	}

	public int getAvgDps() {
		return (int)(totalDamage / getTotalDuration().getSeconds());
	}

	public int getNumCasts(AbilityId abilityId) {
		return getStatistic(abilityId).numCasts;
	}

	private Statistic getStatistic(AbilityId abilityId) {
		return statistics.computeIfAbsent(abilityId, x -> new Statistic());
	}

	private double getDurationPct(Duration duration) {
		return 100 * duration.getSeconds() / getTotalDuration().getSeconds();
	}

	public void setSimulationStart(Time simulationStart) {
		this.simulationStart = simulationStart;
	}

	public void setSimulationEnd(Time simulationEnd) {
		this.simulationEnd = simulationEnd;
	}

	public Duration getTotalDuration() {
		return simulationEnd.subtract(simulationStart);
	}
}

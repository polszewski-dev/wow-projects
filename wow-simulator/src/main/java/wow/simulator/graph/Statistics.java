package wow.simulator.graph;

import wow.commons.model.Duration;
import wow.commons.model.spell.SpellId;
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

	private final Map<SpellId, Statistic> statistics = new LinkedHashMap<>();
	private Time simulationStart;
	private Time simulationEnd;
	private int totalDamage = 0;

	public void addCastTime(SpellId spellId, Duration duration, boolean castFinished) {
		Statistic statistic = getStatistic(spellId);
		statistic.totalCastTime = statistic.totalCastTime.add(duration);
		if (castFinished) {
			++statistic.numCasts;
		}
	}

	public void addCooldownUptime(SpellId spellId, Duration duration) {
		Statistic statistic = getStatistic(spellId);
		statistic.cdUptime = statistic.cdUptime.add(duration);
	}

	public void addEffectUptime(SpellId spellId, Duration duration) {
		Statistic statistic = getStatistic(spellId);
		statistic.effectUptime = statistic.effectUptime.add(duration);
	}

	public void addDamage(SpellId spellId, int damage) {
		getStatistic(spellId).totalDamage += damage;
		this.totalDamage += damage;
	}

	public List<SpellId> getEffects() {
		return statistics.entrySet().stream()
				.filter(x -> !x.getValue().effectUptime.isZero())
				.map(Map.Entry::getKey)
				.sorted(Comparator.comparing(this::getEffectUptimePct).reversed())
				.toList();
	}

	public List<SpellId> getCooldowns() {
		return statistics.entrySet().stream()
				.filter(x -> !x.getValue().cdUptime.isZero())
				.map(Map.Entry::getKey)
				.sorted(Comparator.comparing(this::getCooldownUptimePct).reversed())
				.toList();
	}

	public List<SpellId> getDamageSpells() {
		return statistics.entrySet().stream()
				.filter(x -> x.getValue().totalDamage != 0)
				.map(Map.Entry::getKey)
				.sorted(Comparator.comparing(this::getDps).reversed())
				.toList();
	}

	public double getEffectUptimePct(SpellId spellId) {
		return getDurationPct(getStatistic(spellId).effectUptime);
	}

	public double getCooldownUptimePct(SpellId spellId) {
		return getDurationPct(getStatistic(spellId).cdUptime);
	}

	public int getTotalDamage(SpellId spellId) {
		return getStatistic(spellId).totalDamage;
	}

	public double getTotalDamagePct(SpellId spellId) {
		return 100.0 * getStatistic(spellId).totalDamage / totalDamage;
	}

	public int getTotalDamage() {
		return totalDamage;
	}

	public int getDps(SpellId spellId) {
		Statistic statistic = getStatistic(spellId);
		return (int)(statistic.totalDamage / statistic.totalCastTime.getSeconds());
	}

	public int getAvgDps() {
		return (int)(totalDamage / getTotalDuration().getSeconds());
	}

	public int getNumCasts(SpellId spellId) {
		return getStatistic(spellId).numCasts;
	}

	private Statistic getStatistic(SpellId spellId) {
		return statistics.computeIfAbsent(spellId, x -> new Statistic());
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

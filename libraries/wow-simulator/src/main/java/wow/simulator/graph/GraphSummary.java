package wow.simulator.graph;

import lombok.Getter;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.SpellSchool;
import wow.simulator.model.unit.Player;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2023-08-22
 */
@Getter
public class GraphSummary {
	private final Map<Integer, List<String>> messagesByXOffset = new TreeMap<>();

	public void addSummary(Player player, Statistics statistics) {
		var snapshot = player.getStats();

		int c1 = 0;
		int c2 = 250;
		int c3 = 500;

		for (SpellSchool spellSchool : player.getCharacterClass().getSpellSchools()) {
			addSummary(c1, "SP %-8s %7s", spellSchool + ":", player.getStats().getSpellDamage(spellSchool));
		}
		addSummary(c1, "Hit%%:     %9.2f", snapshot.getSpellHitPct());
		addSummary(c1, "Crit%%:    %9.2f", snapshot.getSpellCritPct());
		addSummary(c1, "Haste%%:   %9.2f", snapshot.getSpellHastePct());
		addSummary(c1, "");

		addSummary(c1, "CD Uptime:");
		for (AbilityId abilityId : statistics.getCooldowns()) {
			addSummary(c1, "- %-20s %6.2f%%", abilityId.getName(), statistics.getCooldownUptimePct(abilityId));
		}
		addSummary(c1, "");

		addSummary(c2, "Total Duration: %11s sec", (int)statistics.getTotalDuration().getSeconds());
		addSummary(c2, "");

		addSummary(c2, "DoT Uptime:");
		for (AbilityId abilityId : statistics.getEffects()) {
			addSummary(c2, "- %-20s %6.2f%%", abilityId.getName(), statistics.getEffectUptimePct(abilityId));
		}
		addSummary(c2, "");

		addSummary(c3, "Total Damage:");
		for (AbilityId abilityId : statistics.getDamageSpells()) {
			addDamageSummary(c3, statistics, abilityId);
		}

		addSummary(c3, "---------------------------------------------------------");
		addSummary(c3, "From All Spells: %,24d | %,6d", statistics.getTotalDamage(), statistics.getAvgDps());
	}

	private void addDamageSummary(int xOffset, Statistics statistics, AbilityId abilityId) {
		addSummary(xOffset, "- %-20s %6.2f%% |  %,7d | %,6d | %4s",
				   abilityId.getName(),
				   statistics.getTotalDamagePct(abilityId),
				   statistics.getTotalDamage(abilityId),
				   statistics.getDps(abilityId),
				   statistics.getNumCasts(abilityId));
	}

	private void addSummary(int xOffset, String msg, Object... args) {
		messagesByXOffset.computeIfAbsent(xOffset, x -> new ArrayList<>()).add(String.format(msg, args));
	}

	public int getMaxSummaryRows() {
		return messagesByXOffset.values().stream().map(List::size).max(Comparator.comparing(x -> x)).orElse(0);
	}
}

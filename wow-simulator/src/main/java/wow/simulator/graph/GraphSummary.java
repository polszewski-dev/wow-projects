package wow.simulator.graph;

import lombok.Getter;
import wow.character.model.snapshot.Snapshot;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
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
		Snapshot snapshot = player.getSnapshot();

		int c1 = 0;
		int c2 = 250;
		int c3 = 500;

		for (SpellSchool spellSchool : player.getCharacter().getCharacterClass().getSpellSchools()) {
			addSummary(c1, "SP %-8s %7s", spellSchool + ":", (int)player.getStats().getTotalSpellDamage(spellSchool));
		}
		addSummary(c1, "Hit%%:     %9.2f", snapshot.getTotalHit());
		addSummary(c1, "Crit%%:    %9.2f", snapshot.getTotalCrit());
		addSummary(c1, "Haste%%:   %9.2f", snapshot.getTotalHaste());
		addSummary(c1, "");

		addSummary(c1, "CD Uptime:");
		for (SpellId spellId : statistics.getCooldowns()) {
			addSummary(c1, "- %-20s %6.2f%%", spellId.getName(), statistics.getCooldownUptimePct(spellId));
		}
		addSummary(c1, "");

		addSummary(c2, "Total Duration: %11s sec", (int)statistics.getTotalDuration().getSeconds());
		addSummary(c2, "");

		addSummary(c2, "DoT Uptime:");
		for (SpellId spellId : statistics.getEffects()) {
			addSummary(c2, "- %-20s %6.2f%%", spellId.getName(), statistics.getEffectUptimePct(spellId));
		}
		addSummary(c2, "");

		addSummary(c3, "Total Damage:");
		for (SpellId spellId : statistics.getDamageSpells()) {
			addDamageSummary(c3, statistics, spellId);
		}

		addSummary(c3, "---------------------------------------------------------");
		addSummary(c3, "From All Spells: %,24d | %,6d", statistics.getTotalDamage(), statistics.getAvgDps());
	}

	private void addDamageSummary(int xOffset, Statistics statistics, SpellId spellId) {
		addSummary(xOffset, "- %-20s %6.2f%% |  %,7d | %,6d | %4s",
				   spellId.getName(),
				   statistics.getTotalDamagePct(spellId),
				   statistics.getTotalDamage(spellId),
				   statistics.getDps(spellId),
				   statistics.getNumCasts(spellId));
	}

	private void addSummary(int xOffset, String msg, Object... args) {
		messagesByXOffset.computeIfAbsent(xOffset, x -> new ArrayList<>()).add(String.format(msg, args));
	}

	public int getMaxSummaryRows() {
		return messagesByXOffset.values().stream().map(List::size).max(Comparator.comparing(x -> x)).orElse(0);
	}
}

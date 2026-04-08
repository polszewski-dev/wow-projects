package wow.commons.util;

import wow.commons.model.config.TimeRestricted;
import wow.commons.model.pve.PhaseId;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * User: POlszewski
 * Date: 2023-11-30
 */
public class PhaseMap<K, V> extends TimeMap<K, V, PhaseId> {
	public static <K, V extends TimeRestricted> void putForEveryPhase(PhaseMap<K, V> map, K id, V value) {
		var earliestPhaseId = value.getEarliestPhaseId();
		map.putForEveryPhase(earliestPhaseId, id, value);
	}

	public static <K, V extends TimeRestricted> void addEntryForEveryPhase(PhaseMap<K, List<V>> map, K key, V value) {
		var earliestPhaseId = value.getEarliestPhaseId();
		map.computeIfAbsentForEveryPhase(earliestPhaseId, key, ArrayList::new, x -> x.add(value));
	}

	public void putForEveryPhase(PhaseId earliestPhaseId, K key, V value) {
		for (var phaseId : earliestPhaseId.getPhasesStartingFromThisOne()) {
			put(phaseId, key, value);
		}
	}

	public void computeIfAbsentForEveryPhase(PhaseId earliestPhaseId, K key, Supplier<V> supplier, Consumer<V> consumer) {
		for (var phaseId : earliestPhaseId.getPhasesStartingFromThisOne()) {
			var value = computeIfAbsent(phaseId, key, supplier);
			consumer.accept(value);
		}
	}

	private static class PhaseEntryMap<V> extends TimeEntryMap<PhaseId, V> {
		private V vanillaP1Value;
		private V vanillaP2Value;
		private V vanillaP25Value;
		private V vanillaP3Value;
		private V vanillaP4Value;
		private V vanillaP5Value;
		private V vanillaP6Value;
		private V tbcP0Value;
		private V tbcP1Value;
		private V tbcP2Value;
		private V tbcP3Value;
		private V tbcP4Value;
		private V tbcP5Value;
		private V wotlkP0Value;
		private V wotlkP1Value;

		@Override
		public V get(PhaseId timeKey) {
			return switch (timeKey) {
				case VANILLA_P1 -> vanillaP1Value;
				case VANILLA_P2 -> vanillaP2Value;
				case VANILLA_P2_5 -> vanillaP25Value;
				case VANILLA_P3 -> vanillaP3Value;
				case VANILLA_P4 -> vanillaP4Value;
				case VANILLA_P5 -> vanillaP5Value;
				case VANILLA_P6 -> vanillaP6Value;
				case TBC_P0 -> tbcP0Value;
				case TBC_P1 -> tbcP1Value;
				case TBC_P2 -> tbcP2Value;
				case TBC_P3 -> tbcP3Value;
				case TBC_P4 -> tbcP4Value;
				case TBC_P5 -> tbcP5Value;
				case WOTLK_P0 -> wotlkP0Value;
				case WOTLK_P1 -> wotlkP1Value;
			};
		}

		@Override
		public void put(PhaseId timeKey, V value) {
			switch (timeKey) {
				case VANILLA_P1 -> vanillaP1Value = value;
				case VANILLA_P2 -> vanillaP2Value = value;
				case VANILLA_P2_5 -> vanillaP25Value = value;
				case VANILLA_P3 -> vanillaP3Value = value;
				case VANILLA_P4 -> vanillaP4Value = value;
				case VANILLA_P5 -> vanillaP5Value = value;
				case VANILLA_P6 -> vanillaP6Value = value;
				case TBC_P0 -> tbcP0Value = value;
				case TBC_P1 -> tbcP1Value = value;
				case TBC_P2 -> tbcP2Value = value;
				case TBC_P3 -> tbcP3Value = value;
				case TBC_P4 -> tbcP4Value = value;
				case TBC_P5 -> tbcP5Value = value;
				case WOTLK_P0 -> wotlkP0Value = value;
				case WOTLK_P1 -> wotlkP1Value = value;
			}
		}

		@Override
		protected PhaseId[] timeKeyValues() {
			return PhaseId.values();
		}
	}

	@Override
	protected TimeEntryMap<PhaseId, V> newTimeEntryMap() {
		return new PhaseEntryMap<>();
	}
}

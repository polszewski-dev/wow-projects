package wow.commons.util;

import wow.commons.model.config.TimeRestricted;
import wow.commons.model.pve.PhaseId;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2023-11-30
 */
public class PhaseMap<K, V> extends TimeMap<K, V, PhaseId> {
	public PhaseMap() {
		super(PhaseId.class);
	}

	public static <K, V extends TimeRestricted> void putForEveryPhase(PhaseMap<K, V> map, K id, V value) {
		var earliestPhaseId = value.getEarliestPhaseId();
		map.putForEveryPhase(earliestPhaseId, id, value);
	}

	public static <K, V extends TimeRestricted> void addEntryForEveryPhase(PhaseMap<K, List<V>> map, K key, V value) {
		var earliestPhaseId = value.getEarliestPhaseId();
		map.computeIfAbsentForEveryPhase(earliestPhaseId, key, x -> new ArrayList<>(), x -> x.add(value));
	}

	public void putForEveryPhase(PhaseId earliestPhaseId, K key, V value) {
		for (var phaseId : earliestPhaseId.getPhasesStartingFromThisOne()) {
			put(phaseId, key, value);
		}
	}

	public void computeIfAbsentForEveryPhase(PhaseId earliestPhaseId, K key, Function<K, V> mappingFunction, Consumer<V> consumer) {
		for (var phaseId : earliestPhaseId.getPhasesStartingFromThisOne()) {
			var value = computeIfAbsent(phaseId, key, mappingFunction);
			consumer.accept(value);
		}
	}
}

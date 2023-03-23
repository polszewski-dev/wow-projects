package wow.commons.repository.impl;

import wow.commons.model.config.TimeRestricted;
import wow.commons.model.pve.Phase;
import wow.commons.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
public abstract class ExcelRepository {
	protected  <K, T extends TimeRestricted> Optional<T> getUnique(Map<K, List<T>> map, K key, Phase phase) {
		return map.getOrDefault(key, List.of()).stream()
				.filter(x -> x.isAvailableDuring(phase))
				.collect(CollectionUtil.toOptionalSingleton());
	}

	protected <K, T extends TimeRestricted> List<T> getList(Map<K, List<T>> map, K key, Phase phase) {
		return map.getOrDefault(key, List.of()).stream()
				.filter(x -> x.isAvailableDuring(phase))
				.toList();
	}

	protected <T extends TimeRestricted> List<T> getList(List<T> list, Phase phase) {
		return list.stream()
				.filter(x -> x.isAvailableDuring(phase))
				.toList();
	}

	protected <K, T> void addEntry(Map<K, List<T>> map, K key, T entry) {
		map.computeIfAbsent(key, x -> new ArrayList<>()).add(entry);
	}
}

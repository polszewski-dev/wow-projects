package wow.character.model.character;

import lombok.AllArgsConstructor;
import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.buff.BuffNameRank;

import java.util.*;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
@AllArgsConstructor
public class Buffs implements EffectCollection, Copyable<Buffs> {
	private final Map<BuffId, Buff> availableBuffsById = new LinkedHashMap<>();
	private final Map<String, List<Buff>> availableBuffsByName = new LinkedHashMap<>();
	private final Map<BuffNameRank, Buff> availableBuffsByNameRank = new LinkedHashMap<>();
	private final Map<String, Buff> enabledBuffsByName = new LinkedHashMap<>();
	private final BuffListType type;

	public List<Buff> getList() {
		return List.copyOf(enabledBuffsByName.values());
	}

	public Stream<Buff> getStream() {
		return enabledBuffsByName.values().stream();
	}

	public List<Buff> getAvailableHighestRanks() {
		return getHighestRanks(availableBuffsByName.keySet()).stream()
				.map(availableBuffsByNameRank::get)
				.toList();
	}

	public boolean has(String name) {
		return enabledBuffsByName.containsKey(name);
	}

	public void setAvailable(List<Buff> availableBuffs) {
		availableBuffsById.clear();
		availableBuffsByName.clear();
		availableBuffsByNameRank.clear();

		for (var buff : availableBuffs) {
			availableBuffsById.put(buff.getId(), buff);
			availableBuffsByName.computeIfAbsent(buff.getName(), x -> new ArrayList<>()).add(buff);
			availableBuffsByNameRank.put(buff.getNameRank(), buff);
		}

		var newEnabledBuffList = getNewEnabledBuffList();

		reset();

		for (BuffNameRank nameRank : newEnabledBuffList) {
			enable(nameRank.name(), nameRank.rank());
		}
	}

	private List<BuffNameRank> getNewEnabledBuffList() {
		var names = enabledBuffsByName.values().stream()
				.map(Buff::getName)
				.toList();

		return getHighestRanks(names);
	}

	public void reset() {
		enabledBuffsByName.clear();
	}

	public void setHighestRanks(Collection<String> buffNames) {
		var nameRanks = getHighestRanks(buffNames);

		set(nameRanks);
	}

	private List<BuffNameRank> getHighestRanks(Collection<String> names) {
		return names.stream()
				.map(this::getHighestRank)
				.flatMap(Optional::stream)
				.toList();
	}

	private Optional<BuffNameRank> getHighestRank(String name) {
		return availableBuffsByName.getOrDefault(name, List.of()).stream()
				.map(Buff::getNameRank)
				.max(Comparator.comparingInt(BuffNameRank::rank));
	}

	public void set(Collection<BuffNameRank> buffNameRanks) {
		reset();

		for (var nameRank : buffNameRanks) {
			enable(nameRank.name(), nameRank.rank());
		}
	}

	public void setBuffIds(Collection<BuffId> buffIds) {
		reset();

		for (var buffId : buffIds) {
			enable(buffId, true);
		}
	}

	public void enable(BuffId buffId, boolean enabled) {
		var buff = getBuff(buffId).orElseThrow();

		enable(buff, enabled);
	}

	public void enable(String name, int rank, boolean enabled) {
		var buff = getBuff(new BuffNameRank(name, rank)).orElseThrow();

		enable(buff, enabled);
	}

	public void enable(String name, int rank) {
		enable(name, rank, true);
	}

	private void enable(Buff buff, boolean enabled) {
		var buffName = buff.getName();

		if (enabled) {
			if (buff.getExclusionGroup() != null) {
				enabledBuffsByName.entrySet().removeIf(e -> e.getValue().getExclusionGroup() == buff.getExclusionGroup());
			}

			enabledBuffsByName.put(buffName, buff);
		} else {
			enabledBuffsByName.remove(buffName);
		}
	}

	public void disable(String name) {
		enabledBuffsByName.remove(name);
	}

	private Optional<Buff> getBuff(BuffId buffId) {
		var buff = availableBuffsById.get(buffId);

		return Optional.ofNullable(buff);
	}

	private Optional<Buff> getBuff(BuffNameRank nameRank) {
		var buff = availableBuffsByNameRank.get(nameRank);

		return Optional.ofNullable(buff);
	}

	private boolean isAvailable(Buff buff) {
		return availableBuffsByNameRank.containsKey(buff.getNameRank());
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		for (var buff : enabledBuffsByName.values()) {
			collector.addEffect(buff.getEffect(), buff.getStacks());
		}
	}

	@Override
	public Buffs copy() {
		var copy = new Buffs(type);
		copy.availableBuffsById.putAll(this.availableBuffsById);
		copy.availableBuffsByNameRank.putAll(this.availableBuffsByNameRank);
		copy.availableBuffsByName.putAll(this.availableBuffsByName);
		copy.enabledBuffsByName.putAll(this.enabledBuffsByName);
		return copy;
	}
}

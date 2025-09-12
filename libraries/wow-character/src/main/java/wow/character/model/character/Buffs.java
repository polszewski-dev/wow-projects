package wow.character.model.character;

import lombok.AllArgsConstructor;
import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.buff.BuffIdAndRank;

import java.util.*;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
@AllArgsConstructor
public class Buffs implements EffectCollection, Copyable<Buffs> {
	private final Map<Integer, Buff> availableBuffsByDbId = new LinkedHashMap<>();
	private final Map<BuffIdAndRank, Buff> availableBuffsById = new LinkedHashMap<>();
	private final Map<BuffId, List<Buff>> availableBuffsByBuffId = new LinkedHashMap<>();
	private final Map<BuffId, Buff> enabledBuffsById = new LinkedHashMap<>();
	private final BuffListType type;

	@Override
	public Buffs copy() {
		var copy = new Buffs(type);
		copy.availableBuffsByDbId.putAll(this.availableBuffsByDbId);
		copy.availableBuffsById.putAll(this.availableBuffsById);
		copy.availableBuffsByBuffId.putAll(this.availableBuffsByBuffId);
		copy.enabledBuffsById.putAll(this.enabledBuffsById);
		return copy;
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		for (Buff buff : enabledBuffsById.values()) {
			collector.addEffect(buff.getEffect(), buff.getStacks());
		}
	}

	public List<Buff> getList() {
		return List.copyOf(enabledBuffsById.values());
	}

	public Stream<Buff> getStream() {
		return enabledBuffsById.values().stream();
	}

	public List<Buff> getAvailableHighestRanks() {
		return getHighestRanks(availableBuffsByBuffId.keySet()).stream()
				.map(availableBuffsById::get)
				.toList();
	}

	public void setAvailable(List<Buff> availableBuffs) {
		availableBuffsByDbId.clear();
		availableBuffsById.clear();
		availableBuffsByBuffId.clear();

		for (var buff : availableBuffs) {
			availableBuffsByDbId.put(buff.getDbId(), buff);
			availableBuffsById.put(buff.getId(), buff);
			availableBuffsByBuffId.computeIfAbsent(buff.getBuffId(), x -> new ArrayList<>()).add(buff);
		}

		enabledBuffsById.entrySet().removeIf(entry -> !isAvailable(entry.getValue()));
	}

	public void reset() {
		enabledBuffsById.clear();
	}

	public void setHighestRanks(Collection<BuffId> buffIds) {
		var buffIdAndRanks = getHighestRanks(buffIds);

		set(buffIdAndRanks);
	}

	private List<BuffIdAndRank> getHighestRanks(Collection<BuffId> buffIds) {
		return buffIds.stream()
				.map(this::getHighestRank)
				.flatMap(Optional::stream)
				.toList();
	}

	private Optional<BuffIdAndRank> getHighestRank(BuffId buffId) {
		return availableBuffsByBuffId.getOrDefault(buffId, List.of()).stream()
				.map(Buff::getId)
				.max(Comparator.comparingInt(BuffIdAndRank::rank));
	}

	public void set(Collection<BuffIdAndRank> buffIdAndRanks) {
		reset();

		for (var buffIdAndRank : buffIdAndRanks) {
			enable(buffIdAndRank.buffId(), buffIdAndRank.rank());
		}
	}

	public void enable(BuffId buffId, int rank, boolean enabled) {
		if (enabled) {
			enable(buffId, rank);
		} else {
			disable(buffId);
		}
	}

	public void enable(BuffId buffId, int rank) {
		var id = new BuffIdAndRank(buffId, rank);
		var buff = getBuff(id).orElseThrow();

		if (buff.getExclusionGroup() != null) {
			enabledBuffsById.entrySet().removeIf(e -> e.getValue().getExclusionGroup() == buff.getExclusionGroup());
		}

		enabledBuffsById.put(buff.getBuffId(), buff);
	}

	public void disable(BuffId buffId) {
		enabledBuffsById.remove(buffId);
	}

	public void enable(int buffId, boolean enabled) {
		var buff = getBuff(buffId).orElseThrow();

		enable(buff.getBuffId(), buff.getRank(), enabled);
	}

	private Optional<Buff> getBuff(BuffIdAndRank id) {
		var buff = availableBuffsById.get(id);

		return Optional.ofNullable(buff);
	}

	private Optional<Buff> getBuff(int buffId) {
		var buff = availableBuffsByDbId.get(buffId);

		return Optional.ofNullable(buff);
	}

	public boolean has(BuffId buffId) {
		return enabledBuffsById.containsKey(buffId);
	}

	private boolean isAvailable(Buff buff) {
		return availableBuffsById.containsKey(buff.getId());
	}
}

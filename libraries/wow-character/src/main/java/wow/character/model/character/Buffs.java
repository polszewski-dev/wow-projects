package wow.character.model.character;

import lombok.AllArgsConstructor;
import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;

import java.util.*;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
@AllArgsConstructor
public class Buffs extends Options<Buff, BuffId> implements EffectCollection, Copyable<Buffs> {
	private final Map<BuffId, Buff> availableBuffsById = new LinkedHashMap<>();
	private final Map<String, Buff> availableBuffsByName = new LinkedHashMap<>();
	private final Map<String, Buff> enabledBuffsByName = new LinkedHashMap<>();
	private final BuffListType type;

	public List<Buff> getList() {
		return List.copyOf(enabledBuffsByName.values());
	}

	public Stream<Buff> getStream() {
		return enabledBuffsByName.values().stream();
	}

	@Override
	public List<Buff> getAvailable() {
		return List.copyOf(availableBuffsByName.values());
	}

	public boolean has(String name) {
		return enabledBuffsByName.containsKey(name);
	}

	public void setAvailable(List<Buff> availableBuffs) {
		availableBuffsById.clear();
		availableBuffsByName.clear();

		for (var buff : availableBuffs) {
			availableBuffsById.put(buff.getId(), buff);
			availableBuffsByName.put(buff.getName(), buff);
		}

		var namesToEnable = getEnabledNames();

		reset();

		setNames(namesToEnable);
	}

	private List<String> getEnabledNames() {
		return enabledBuffsByName.values().stream()
				.map(Buff::getName)
				.toList();
	}

	public void reset() {
		enabledBuffsByName.clear();
	}

	public void setIds(Collection<BuffId> buffIds) {
		reset();

		for (var buffId : buffIds) {
			enable(buffId);
		}
	}

	public void setNames(Collection<String> names) {
		reset();

		for (var name : names) {
			if (isAvailable(name)) {
				enable(name);
			}
		}
	}

	private boolean isAvailable(String name) {
		return availableBuffsByName.containsKey(name);
	}

	public void enable(BuffId buffId, boolean enabled) {
		var buff = getBuff(buffId).orElseThrow();

		enable(buff, enabled);
	}

	public void enable(String name, boolean enabled) {
		var buff = getBuff(name).orElseThrow();

		enable(buff, enabled);
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

	private Optional<Buff> getBuff(BuffId buffId) {
		var buff = availableBuffsById.get(buffId);

		return Optional.ofNullable(buff);
	}

	private Optional<Buff> getBuff(String name) {
		var buff = availableBuffsByName.get(name);

		return Optional.ofNullable(buff);
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
		copy.availableBuffsByName.putAll(this.availableBuffsByName);
		copy.enabledBuffsByName.putAll(this.enabledBuffsByName);
		return copy;
	}
}

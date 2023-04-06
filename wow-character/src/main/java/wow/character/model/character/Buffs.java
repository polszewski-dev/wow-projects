package wow.character.model.character;

import lombok.AllArgsConstructor;
import wow.character.model.Copyable;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.buffs.Buff;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
@AllArgsConstructor
public class Buffs implements AttributeCollection, Copyable<Buffs> {
	private final Map<Integer, Buff> buffsById = new LinkedHashMap<>();
	private final BuffListType type;

	@Override
	public Buffs copy() {
		Buffs copy = new Buffs(type);
		copy.buffsById.putAll(this.buffsById);
		return copy;
	}

	@Override
	public void collectAttributes(AttributeCollector collector) {
		for (Buff buff : buffsById.values()) {
			collector.addAttributes(buff, buff.getSourceSpell());
		}
	}

	public Collection<Buff> getList() {
		return buffsById.values();
	}

	public void reset() {
		buffsById.clear();
	}

	public void set(Collection<Buff> buffs) {
		reset();

		for (Buff buff : buffs) {
			enable(buff, true);
		}
	}

	public void enable(Buff buff, boolean enable) {
		assertMeetsFilter(buff);

		if (!enable) {
			buffsById.remove(buff.getId());
			return;
		}

		if (has(buff)) {
			return;
		}

		if (buff.getExclusionGroup() != null) {
			buffsById.entrySet().removeIf(e -> e.getValue().getExclusionGroup() == buff.getExclusionGroup());
		}

		buffsById.put(buff.getId(), buff);
	}

	public boolean has(int buffId) {
		return buffsById.containsKey(buffId);
	}

	public boolean has(Buff buff) {
		return has(buff.getId());
	}

	private void assertMeetsFilter(Buff buff) {
		if (!type.getFilter().test(buff)) {
			throw new IllegalArgumentException(buff.getName());
		}
	}
}

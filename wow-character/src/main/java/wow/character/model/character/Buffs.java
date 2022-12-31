package wow.character.model.character;

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
public class Buffs implements AttributeCollection, Copyable<Buffs> {
	private final Map<Integer, Buff> buffsById = new LinkedHashMap<>();

	@Override
	public Buffs copy() {
		Buffs copy = new Buffs();
		copy.buffsById.putAll(this.buffsById);
		return copy;
	}

	@Override
	public <T extends AttributeCollector<T>> void collectAttributes(T collector) {
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

	public void setBuffs(Collection<Buff> buffs) {
		this.buffsById.clear();

		for (Buff buff : buffs) {
			enableBuff(buff, true);
		}
	}

	public void enableBuff(Buff buff, boolean enable) {
		if (!enable) {
			buffsById.remove(buff.getId());
			return;
		}

		if (hasBuff(buff)) {
			return;
		}

		if (buff.getExclusionGroup() != null) {
			buffsById.entrySet().removeIf(e -> e.getValue().getExclusionGroup() == buff.getExclusionGroup());
		}

		buffsById.put(buff.getId(), buff);
	}

	public boolean hasBuff(Buff buff) {
		return buffsById.containsKey(buff.getId());
	}
}

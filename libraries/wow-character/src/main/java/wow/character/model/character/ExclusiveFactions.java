package wow.character.model.character;

import wow.character.model.Copyable;
import wow.commons.model.character.ExclusiveFaction;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-05-13
 */
public class ExclusiveFactions implements Copyable<ExclusiveFactions> {
	private final Map<ExclusiveFaction.Group, ExclusiveFaction> map = new EnumMap<>(ExclusiveFaction.Group.class);

	@Override
	public ExclusiveFactions copy() {
		ExclusiveFactions copy = new ExclusiveFactions();
		copy.map.putAll(map);
		return copy;
	}

	public boolean has(ExclusiveFaction exclusiveFaction) {
		return map.get(exclusiveFaction.getGroup()) == exclusiveFaction;
	}

	public void set(List<ExclusiveFaction> exclusiveFactions) {
		map.clear();
		for (ExclusiveFaction exclusiveFaction : exclusiveFactions) {
			map.put(exclusiveFaction.getGroup(), exclusiveFaction);
		}
	}

	public List<ExclusiveFaction> getList() {
		return new ArrayList<>(map.values());
	}
}

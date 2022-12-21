package wow.character.model.build;

import lombok.AllArgsConstructor;
import wow.commons.model.buffs.Buff;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
@AllArgsConstructor
public class BuffSets {
	private final Map<BuffSetId, List<Buff>> buffSetById;

	public static final BuffSets EMPTY = new BuffSets(Map.of());

	public List<Buff> getBuffSet(BuffSetId buffSetId) {
		return buffSetById.getOrDefault(buffSetId, List.of());
	}
}

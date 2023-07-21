package wow.commons.model.sources;

import wow.commons.model.pve.Npc;
import wow.commons.model.pve.Zone;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public record NpcDrop(
		Npc npc,
		List<Zone> zones
) implements Source {
	@Override
	public boolean isNpcDrop() {
		return true;
	}

	@Override
	public String toString() {
		return "Npc: " + npc;
	}
}

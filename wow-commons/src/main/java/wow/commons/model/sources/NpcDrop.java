package wow.commons.model.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.pve.Npc;
import wow.commons.model.pve.Zone;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class NpcDrop extends Source {
	private final Npc npc;
	private final Zone zone;

	@Override
	public boolean isNpcDrop() {
		return true;
	}

	@Override
	public String toString() {
		return "Npc: " + npc;
	}
}

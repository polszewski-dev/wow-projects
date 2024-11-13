package wow.simulator.model.stats;

import lombok.Getter;
import wow.commons.model.spell.CooldownId;
import wow.simulator.model.time.Time;

/**
 * User: POlszewski
 * Date: 2024-11-12
 */
@Getter
public final class CooldownTimeEntry extends TimeEntry {
	private final CooldownId cooldownId;

	public CooldownTimeEntry(CooldownId cooldownId, Time begin) {
		super(begin);
		this.cooldownId = cooldownId;
	}
}

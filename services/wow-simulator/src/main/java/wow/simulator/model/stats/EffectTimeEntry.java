package wow.simulator.model.stats;

import lombok.Getter;
import wow.simulator.model.time.Time;

/**
 * User: POlszewski
 * Date: 2024-11-12
 */
@Getter
public final class EffectTimeEntry extends TimeEntry {
	private final int effectId;

	public EffectTimeEntry(int effectId, Time begin) {
		super(begin);
		this.effectId = effectId;
	}
}

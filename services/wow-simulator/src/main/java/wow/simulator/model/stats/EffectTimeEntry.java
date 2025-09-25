package wow.simulator.model.stats;

import lombok.Getter;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectId;
import wow.simulator.model.time.Time;

/**
 * User: POlszewski
 * Date: 2024-11-12
 */
@Getter
public final class EffectTimeEntry extends TimeEntry {
	private final Effect effect;

	public EffectTimeEntry(Effect effect, Time begin) {
		super(begin);
		this.effect = effect;
	}

	public EffectId getEffectId() {
		return effect.getId();
	}
}

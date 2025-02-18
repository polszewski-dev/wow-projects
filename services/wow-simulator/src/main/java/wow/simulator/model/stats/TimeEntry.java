package wow.simulator.model.stats;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.simulator.model.time.Time;

/**
 * User: POlszewski
 * Date: 2023-08-22
 */
@Getter
@Setter
public abstract sealed class TimeEntry permits AbilityTimeEntry, EffectTimeEntry, CooldownTimeEntry {
	protected final Time begin;
	protected Time end;

	protected TimeEntry(Time begin) {
		this.begin = begin;
	}

	public Duration getElapsedTime() {
		return end.subtract(begin);
	}

	public void complete(Time time) {
		if (end == null) {
			this.end = time;
		}
	}
}

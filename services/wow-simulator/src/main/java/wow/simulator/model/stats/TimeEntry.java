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
	private final Time begin;
	private Time end;
	private Time gcdEnd;

	protected TimeEntry(Time begin) {
		this.begin = begin;
	}

	public Duration getElapsedTime() {
		return end.max(gcdEnd).subtract(begin);
	}

	public void complete(Time time) {
		if (end == null) {
			this.end = time;
		}
		if (gcdEnd == null) {
			this.gcdEnd = time;
		}
	}

	public boolean isComplete() {
		return end != null && gcdEnd != null;
	}
}

package wow.simulator.graph;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.commons.model.spell.SpellId;
import wow.simulator.model.time.Time;

/**
 * User: POlszewski
 * Date: 2023-08-22
 */
@Getter
@Setter
public class TimeEntry {
	private final SpellId spell;
	private final Time begin;
	private Time end;
	private Time gcdEnd;

	public TimeEntry(SpellId spell, Time begin) {
		this.spell = spell;
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

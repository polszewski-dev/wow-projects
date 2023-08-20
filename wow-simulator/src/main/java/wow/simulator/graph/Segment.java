package wow.simulator.graph;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.simulator.model.time.Time;
import wow.simulator.util.SimpleId;

import java.awt.*;

/**
 * User: POlszewski
 * Date: 2023-08-21
 */
@Getter
@Setter
public class Segment {
	private final SimpleId id;
	private final Time begin;
	private Time end;
	private Time gcdEnd;
	private final Color color;
	private boolean resisted;

	public Segment(SimpleId segmentId, Time time, Color color) {
		this.id = segmentId;
		this.begin = time;
		this.color = color;
	}

	public Time getActualEnd() {
		return end.max(gcdEnd);
	}

	public Duration getActualDuration() {
		return getActualEnd().subtract(begin);
	}

	public Duration getGdcAdjustment() {
		return hasGcdMarker() ? gcdEnd.subtract(end) : Duration.ZERO;
	}

	public boolean hasGcdMarker() {
		return gcdEnd.compareTo(end) > 0;
 	}

	public void endUnfinishedSegment(Time time) {
		if (this.end == null) {
			this.end = time;
		}
		if (this.gcdEnd == null) {
			this.gcdEnd = this.end;
		}
	}
}

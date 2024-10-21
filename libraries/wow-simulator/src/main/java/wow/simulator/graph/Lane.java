package wow.simulator.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.time.Time;
import wow.simulator.util.SimpleId;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-08-21
 */
@AllArgsConstructor
@Getter
public class Lane {
	private final int laneId;
	private final Object objectId;
	private final String label;
	private final Color defaultColor;

	private final Map<SimpleId, Segment> segmentById = new LinkedHashMap<>();

	public boolean matches(AbilityId abilityId) {
		return objectId.toString().equals(abilityId.getName());
	}

	public void addSegment(SimpleId segmentId, Time time, Color color) {
		if (segmentById.containsKey(segmentId)) {
			throw new IllegalArgumentException("Segment already defined: " + segmentId);
		}

		Color segmentColor = color != null ? color : this.defaultColor;
		Segment segment = new Segment(segmentId, time, segmentColor);
		segmentById.put(segment.getId(), segment);
	}

	public void endSegment(SimpleId segmentId, Time time) {
		getSegment(segmentId).setEnd(time);
	}

	public void gcdEndSegment(SimpleId segmentId, Time time) {
		getSegment(segmentId).setGcdEnd(time);
	}

	public void addResistedMark(SimpleId segmentId) {
		getSegment(segmentId).setResisted(true);
	}

	public void endUnfinishedSegments(Time time) {
		segmentById.values().forEach(segment -> segment.endUnfinishedSegment(time));
	}

	private Segment getSegment(SimpleId segmentId) {
		return segmentById.get(segmentId);
	}

	public List<Segment> getSegments() {
		var comparator = Comparator
				.comparing(Segment::getBegin)
				.thenComparingLong(segment -> segment.getId().getValue());

		return segmentById.values().stream()
				.sorted(comparator)
				.toList();
	}
}

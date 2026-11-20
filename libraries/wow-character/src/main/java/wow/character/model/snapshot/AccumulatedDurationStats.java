package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
@Getter
public class AccumulatedDurationStats extends AccumulatedPartialStats {
	private double duration;
	private double durationPct;
	private double hasteRating;
	private double hastePct;

	public AccumulatedDurationStats(AttributeConditionChecker checker) {
		super(checker);
	}

	private AccumulatedDurationStats(AccumulatedDurationStats stats) {
		super(stats);
		this.duration = stats.duration;
		this.durationPct = stats.durationPct;
		this.hasteRating = stats.hasteRating;
		this.hastePct = stats.hastePct;
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case DURATION:
				this.duration += getValue(attribute, scaleFactor);
				break;
			case DURATION_PCT:
				this.durationPct += getValue(attribute, scaleFactor);
				break;
			case HASTE_RATING:
				this.hasteRating += getValue(attribute, scaleFactor);
				break;
			case HASTE_PCT:
				this.hastePct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedDurationStats copy() {
		return new AccumulatedDurationStats(this);
	}
}

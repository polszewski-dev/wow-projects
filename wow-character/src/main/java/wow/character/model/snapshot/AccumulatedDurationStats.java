package wow.character.model.snapshot;

import lombok.Getter;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.condition.AttributeConditionArgs;

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

	public AccumulatedDurationStats(AttributeConditionArgs conditionArgs, int characterLevel) {
		super(conditionArgs, characterLevel);
	}

	private AccumulatedDurationStats(AccumulatedDurationStats stats) {
		super(stats);
		this.duration = stats.duration;
		this.durationPct = stats.durationPct;
		this.hasteRating = stats.hasteRating;
		this.hastePct = stats.hastePct;
	}

	@Override
	public void accumulateAttribute(AttributeId id, double value) {
		switch (id) {
			case DURATION:
				this.duration += value;
				break;
			case DURATION_PCT:
				this.durationPct += value;
				break;
			case HASTE_RATING:
				this.hasteRating += value;
				break;
			case HASTE_PCT:
				this.hastePct += value;
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedDurationStats copy() {
		return new AccumulatedDurationStats(this);
	}
}

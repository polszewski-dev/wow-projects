package wow.character.model.snapshot;

import lombok.Getter;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.condition.AttributeConditionArgs;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
@Getter
public class AccumulatedHitStats extends AccumulatedPartialStats {
	private double hitRating;
	private double hitPct;

	public AccumulatedHitStats(AttributeConditionArgs conditionArgs, int characterLevel) {
		super(conditionArgs, characterLevel);
	}

	private AccumulatedHitStats(AccumulatedHitStats stats) {
		super(stats);
		this.hitRating = stats.hitRating;
		this.hitPct = stats.hitPct;
	}

	@Override
	public void accumulateAttribute(AttributeId id, double value) {
		switch (id) {
			case HIT_RATING:
				this.hitRating += value;
				break;
			case HIT_PCT:
				this.hitPct += value;
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedHitStats copy() {
		return new AccumulatedHitStats(this);
	}
}

package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.AttributeId;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
@Getter
public class AccumulatedHitStats extends AccumulatedPartialStats {
	private double hitRating;
	private double hitPct;

	public AccumulatedHitStats(AttributeConditionArgs conditionArgs) {
		super(conditionArgs);
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
			case PARTY_HIT_PCT:
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

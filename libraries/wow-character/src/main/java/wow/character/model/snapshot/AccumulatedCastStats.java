package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.AttributeId;

/**
 * User: POlszewski
 * Date: 2023-10-21
 */
@Getter
public class AccumulatedCastStats extends AccumulatedPartialStats {
	private double hasteRating;
	private double hastePct;
	private double castTime;
	private double castTimePct;

	public AccumulatedCastStats(AttributeConditionArgs conditionArgs) {
		super(conditionArgs);
	}

	private AccumulatedCastStats(AccumulatedCastStats stats) {
		super(stats);
		this.hasteRating = stats.hasteRating;
		this.hastePct = stats.hastePct;
		this.castTime = stats.castTime;
		this.castTimePct = stats.castTimePct;
	}

	@Override
	public void accumulateAttribute(AttributeId id, double value) {
		switch (id) {
			case HASTE_RATING:
				this.hasteRating += value;
				break;
			case HASTE_PCT:
				this.hastePct += value;
				break;
			case CAST_TIME:
				this.castTime += value;
				break;
			case CAST_TIME_PCT:
				this.castTimePct += value;
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedCastStats copy() {
		return new AccumulatedCastStats(this);
	}
}

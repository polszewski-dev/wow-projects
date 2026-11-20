package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;

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

	public AccumulatedCastStats(AttributeConditionChecker checker) {
		super(checker);
	}

	private AccumulatedCastStats(AccumulatedCastStats stats) {
		super(stats);
		this.hasteRating = stats.hasteRating;
		this.hastePct = stats.hastePct;
		this.castTime = stats.castTime;
		this.castTimePct = stats.castTimePct;
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case HASTE_RATING:
				this.hasteRating += getValue(attribute, scaleFactor);
				break;
			case HASTE_PCT:
				this.hastePct += getValue(attribute, scaleFactor);
				break;
			case CAST_TIME:
				this.castTime += getValue(attribute, scaleFactor);
				break;
			case CAST_TIME_PCT:
				this.castTimePct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedCastStats copy() {
		return new AccumulatedCastStats(this);
	}
}

package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.effect.component.StatConversionType;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
@Getter
public class AccumulatedHitStats extends AccumulatedPartialStats {
	private double hitRating;
	private double hitPct;

	public AccumulatedHitStats(AttributeConditionChecker checker) {
		super(checker);
	}

	private AccumulatedHitStats(AccumulatedHitStats stats) {
		super(stats);
		this.hitRating = stats.hitRating;
		this.hitPct = stats.hitPct;
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case HIT_RATING:
				this.hitRating += getValue(attribute, scaleFactor);
				break;
			case HIT_PCT:
				this.hitPct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedHitStats copy() {
		return new AccumulatedHitStats(this);
	}

	@Override
	protected double getValueFrom(StatConversionType type) {
		return switch (type) {
			case MASTER_HIT_PCT_TO_HIT_PCT

					-> hitPct;

			case MASTER_HIT_RATING_TO_HIT_RATING

					-> hitRating;

			default

					-> super.getValueFrom(type);
		};
	}
}

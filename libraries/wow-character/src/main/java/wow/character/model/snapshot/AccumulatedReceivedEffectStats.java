package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public class AccumulatedReceivedEffectStats extends AccumulatedPartialStats {
	private double receivedEffectDuration;
	private double receivedEffectDurationPct;

	public AccumulatedReceivedEffectStats(AttributeConditionChecker checker) {
		super(checker);
	}

	private AccumulatedReceivedEffectStats(AccumulatedReceivedEffectStats stats) {
		super(stats);
		this.receivedEffectDuration = stats.receivedEffectDuration;
		this.receivedEffectDurationPct = stats.receivedEffectDurationPct;
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case RECEIVED_EFFECT_DURATION:
				this.receivedEffectDuration += getValue(attribute, scaleFactor);
				break;
			case RECEIVED_EFFECT_DURATION_PCT:
				this.receivedEffectDurationPct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedReceivedEffectStats copy() {
		return new AccumulatedReceivedEffectStats(this);
	}
}

package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.AttributeId;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public class AccumulatedReceivedEffectStats extends AccumulatedPartialStats {
	private double receivedEffectDuration;
	private double receivedEffectDurationPct;

	public AccumulatedReceivedEffectStats(AttributeConditionArgs conditionArgs) {
		super(conditionArgs);
	}

	private AccumulatedReceivedEffectStats(AccumulatedReceivedEffectStats stats) {
		super(stats);
		this.receivedEffectDuration = stats.receivedEffectDuration;
		this.receivedEffectDurationPct = stats.receivedEffectDurationPct;
	}

	@Override
	public void accumulateAttribute(AttributeId id, double value) {
		switch (id) {
			case RECEIVED_EFFECT_DURATION:
				this.receivedEffectDuration += value;
				break;
			case RECEIVED_EFFECT_DURATION_PCT:
				this.receivedEffectDurationPct += value;
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedReceivedEffectStats copy() {
		return new AccumulatedReceivedEffectStats(this);
	}
}

package wow.character.model.snapshot;

import lombok.Getter;
import wow.commons.model.attribute.condition.AttributeConditionArgs;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public class AccumulatedTargetStats extends AccumulatedPartialStats {
	private double damageTaken;
	private double damageTakenPct;
	private double powerTaken;
	private double critTakenPct;
	private double receivedEffectDuration;
	private double receivedEffectDurationPct;

	public AccumulatedTargetStats(AttributeConditionArgs conditionArgs, int characterLevel) {
		super(conditionArgs, characterLevel);
	}

	private AccumulatedTargetStats(AccumulatedTargetStats stats) {
		super(stats);
		this.damageTaken = stats.damageTaken;
		this.damageTakenPct = stats.damageTakenPct;
		this.powerTaken = stats.powerTaken;
		this.critTakenPct = stats.critTakenPct;
		this.receivedEffectDuration = stats.receivedEffectDuration;
		this.receivedEffectDurationPct = stats.receivedEffectDurationPct;
	}

	@Override
	public void accumulateAttribute(PrimitiveAttributeId id, double value) {
		switch (id) {
			case DAMAGE_TAKEN:
				this.damageTaken += value;
				break;
			case DAMAGE_TAKEN_PCT:
				this.damageTakenPct += value;
				break;
			case POWER_TAKEN:
				this.powerTaken += value;
				break;
			case CRIT_TAKEN_PCT:
				this.critTakenPct += value;
				break;
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

	public AccumulatedTargetStats copy() {
		return new AccumulatedTargetStats(this);
	}
}

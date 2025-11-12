package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.AttributeId;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public class AccumulatedTargetStats extends AccumulatedPartialStats {
	private double amountTaken;
	private double amountTakenPct;
	private double powerTaken;
	private double critTakenPct;

	public AccumulatedTargetStats(AttributeConditionArgs conditionArgs) {
		super(conditionArgs);
	}

	private AccumulatedTargetStats(AccumulatedTargetStats stats) {
		super(stats);
		this.amountTaken = stats.amountTaken;
		this.amountTakenPct = stats.amountTakenPct;
		this.powerTaken = stats.powerTaken;
		this.critTakenPct = stats.critTakenPct;
	}

	@Override
	public void accumulateAttribute(AttributeId id, double value) {
		switch (id) {
			case DAMAGE_TAKEN:
				if (isDamage) {
					this.amountTaken += value;
				}
				break;
			case DAMAGE_TAKEN_PCT:
				if (isDamage) {
					this.amountTakenPct += value;
				}
				break;
			case HEALING_TAKEN:
				if (isHealing) {
					this.amountTaken += value;
				}
				break;
			case HEALING_TAKEN_PCT:
				if (isHealing) {
					this.amountTakenPct += value;
				}
				break;
			case POWER_TAKEN:
				this.powerTaken += value;
				break;
			case CRIT_TAKEN_PCT:
				this.critTakenPct += value;
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedTargetStats copy() {
		return new AccumulatedTargetStats(this);
	}
}

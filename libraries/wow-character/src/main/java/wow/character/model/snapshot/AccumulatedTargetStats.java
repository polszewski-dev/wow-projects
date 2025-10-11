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
	private double damageTaken;
	private double damageTakenPct;
	private double powerTaken;
	private double critTakenPct;

	public AccumulatedTargetStats(AttributeConditionArgs conditionArgs) {
		super(conditionArgs);
	}

	private AccumulatedTargetStats(AccumulatedTargetStats stats) {
		super(stats);
		this.damageTaken = stats.damageTaken;
		this.damageTakenPct = stats.damageTakenPct;
		this.powerTaken = stats.powerTaken;
		this.critTakenPct = stats.critTakenPct;
	}

	@Override
	public void accumulateAttribute(AttributeId id, double value) {
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
			default:
				// ignore the rest
		}
	}

	public AccumulatedTargetStats copy() {
		return new AccumulatedTargetStats(this);
	}
}

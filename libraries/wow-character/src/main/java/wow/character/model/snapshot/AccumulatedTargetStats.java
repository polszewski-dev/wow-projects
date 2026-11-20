package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;

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

	public AccumulatedTargetStats(AttributeConditionChecker checker) {
		super(checker);
	}

	private AccumulatedTargetStats(AccumulatedTargetStats stats) {
		super(stats);
		this.amountTaken = stats.amountTaken;
		this.amountTakenPct = stats.amountTakenPct;
		this.powerTaken = stats.powerTaken;
		this.critTakenPct = stats.critTakenPct;
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case DAMAGE_TAKEN:
				if (isDamage) {
					this.amountTaken += getValue(attribute, scaleFactor);
				}
				break;
			case DAMAGE_TAKEN_PCT:
				if (isDamage) {
					this.amountTakenPct += getValue(attribute, scaleFactor);
				}
				break;
			case HEALING_TAKEN:
				if (isHealing) {
					this.amountTaken += getValue(attribute, scaleFactor);
				}
				break;
			case HEALING_TAKEN_PCT:
				if (isHealing) {
					this.amountTakenPct += getValue(attribute, scaleFactor);
				}
				break;
			case POWER_TAKEN:
				this.powerTaken += getValue(attribute, scaleFactor);
				break;
			case CRIT_TAKEN_PCT:
				this.critTakenPct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedTargetStats copy() {
		return new AccumulatedTargetStats(this);
	}
}

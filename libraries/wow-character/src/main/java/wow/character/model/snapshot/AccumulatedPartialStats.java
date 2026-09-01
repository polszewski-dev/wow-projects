package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.effect.component.StatConversion;

import static wow.character.util.AttributeConditionChecker.check;
import static wow.character.util.StatConversionConditionChecker.check;
import static wow.commons.model.attribute.PowerType.HEALING;

/**
 * User: POlszewski
 * Date: 2023-11-16
 */
public abstract class AccumulatedPartialStats extends AccumulatedStats {
	@Getter
	protected final AttributeConditionArgs conditionArgs;
	protected final boolean isDamage;
	protected final boolean isHealing;

	protected AccumulatedPartialStats(AttributeConditionArgs conditionArgs) {
		super(conditionArgs);
		this.conditionArgs = conditionArgs;
		this.isDamage = conditionArgs.getPowerType() != null && conditionArgs.getPowerType() != HEALING;
		this.isHealing = conditionArgs.getPowerType() == HEALING;
	}

	protected AccumulatedPartialStats(AccumulatedPartialStats stats) {
		this(stats.conditionArgs);
	}

	@Override
	protected void accumulateAttribute(Attribute attribute, double scaleFactor) {
		if(!check(attribute.condition(), conditionArgs)) {
			return;
		}

		var id = attribute.id();
		var value = scaleFactor * attribute.getScaledValue(scalingParams);

		accumulateAttribute(id, value);
	}

	public abstract void accumulateAttribute(AttributeId id, double value);

	@Override
	protected boolean toConditionMatches(StatConversion statConversion) {
		return check(statConversion.toCondition(), conditionArgs);
	}

	public void accumulateAttribute(AttributeId id, double value, AttributeCondition condition) {
		if (check(condition, conditionArgs)) {
			accumulateAttribute(id, value);
		}
	}
}

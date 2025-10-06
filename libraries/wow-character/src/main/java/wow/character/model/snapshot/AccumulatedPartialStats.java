package wow.character.model.snapshot;

import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.effect.component.StatConversion;

import static wow.character.util.AttributeConditionChecker.check;

/**
 * User: POlszewski
 * Date: 2023-11-16
 */
public abstract class AccumulatedPartialStats extends AccumulatedStats {
	protected final AttributeConditionArgs conditionArgs;

	protected AccumulatedPartialStats(AttributeConditionArgs conditionArgs) {
		super(conditionArgs);
		this.conditionArgs = conditionArgs;
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

	public void accumulateAttribute(AttributeId id, double value, AttributeCondition condition) {
		if (check(condition, conditionArgs)) {
			accumulateAttribute(id, value);
		}
	}

	@Override
	protected void accumulateConvertedStat(StatConversion statConversion, BaseStatsSnapshot baseStats) {
		if (!check(statConversion.toCondition(), conditionArgs)) {
			return;
		}

		var valueFrom = getAccumulatedValue(statConversion.from(), baseStats);
		var ratio = statConversion.ratioPct().value() / 100;
		var valueTo = valueFrom * ratio;

		accumulateAttribute(statConversion.to(), valueTo);
	}

	public AttributeConditionArgs getConditionArgs() {
		return conditionArgs;
	}
}

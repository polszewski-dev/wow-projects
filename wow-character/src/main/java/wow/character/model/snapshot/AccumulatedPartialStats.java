package wow.character.model.snapshot;

import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.condition.AttributeConditionArgs;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.effect.component.StatConversion;

/**
 * User: POlszewski
 * Date: 2023-11-16
 */
public abstract class AccumulatedPartialStats extends AccumulatedStats {
	protected final AttributeConditionArgs conditionArgs;

	protected AccumulatedPartialStats(AttributeConditionArgs conditionArgs, int characterLevel) {
		super(characterLevel);
		this.conditionArgs = conditionArgs;
	}

	protected AccumulatedPartialStats(AccumulatedPartialStats stats) {
		this(stats.conditionArgs, stats.characterLevel);
	}

	@Override
	protected void accumulateAttribute(PrimitiveAttribute attribute, double scaleFactor) {
		if(!attribute.condition().test(conditionArgs)) {
			return;
		}

		var id = attribute.id();
		var value = scaleFactor * attribute.getLevelScaledValue(characterLevel);

		accumulateAttribute(id, value);
	}

	public abstract void accumulateAttribute(PrimitiveAttributeId id, double value);

	public void accumulateAttribute(PrimitiveAttributeId id, double value, AttributeCondition condition) {
		if (condition.test(conditionArgs)) {
			accumulateAttribute(id, value);
		}
	}

	@Override
	protected void accumulateConvertedStat(StatConversion statConversion, BaseStatsSnapshot baseStats) {
		if (!statConversion.toCondition().test(conditionArgs)) {
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

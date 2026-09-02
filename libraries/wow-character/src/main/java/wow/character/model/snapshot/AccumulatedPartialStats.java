package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.effect.component.StatConversion;
import wow.commons.model.effect.component.StatConversionType;

import java.util.List;

import static wow.character.util.AttributeConditionChecker.check;
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

	public void accumulateAttribute(AttributeId id, double value, AttributeCondition condition) {
		if (check(condition, conditionArgs)) {
			accumulateAttribute(id, value);
		}
	}

	public void accumulateConvertedAttributes(List<StatConversion> statConversions) {
		accumulateConvertedAttributes(statConversions, this);
	}

	public void accumulateConvertedAttributes(List<StatConversion> statConversions, AccumulatedPartialStats otherStats) {
		for (var conversion : statConversions) {
			accumulateConvertedAttribute(conversion, otherStats);
		}
	}

	public void accumulateConvertedAttribute(StatConversion conversion) {
		accumulateConvertedAttribute(conversion, this);
	}

	private void accumulateConvertedAttribute(StatConversion conversion, AccumulatedPartialStats otherStats) {
		var to = conversion.to();
		var ratio = conversion.ratioPct().value() / 100;
		var valueFrom = otherStats.getValueFrom(conversion.type());
		var valueTo = valueFrom * ratio;

		accumulateAttribute(to, valueTo);
	}

	protected double getValueFrom(StatConversionType type) {
		throw new IllegalArgumentException("" + type);
	}
}

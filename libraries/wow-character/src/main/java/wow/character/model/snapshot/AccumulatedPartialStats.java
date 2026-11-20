package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.effect.component.StatConversion;
import wow.commons.model.effect.component.StatConversionType;

import java.util.List;

import static wow.commons.model.attribute.PowerType.HEALING;

/**
 * User: POlszewski
 * Date: 2023-11-16
 */
public abstract class AccumulatedPartialStats extends AccumulatedStats {
	@Getter
	protected final AttributeConditionChecker checker;
	protected final boolean isDamage;
	protected final boolean isHealing;

	protected AccumulatedPartialStats(AttributeConditionChecker checker) {
		super(checker);
		this.checker = checker;
		this.isDamage = checker.getPowerType() != null && checker.getPowerType() != HEALING;
		this.isHealing = checker.getPowerType() == HEALING;
	}

	protected AccumulatedPartialStats(AccumulatedPartialStats stats) {
		this(stats.checker);
	}

	@Override
	protected void accumulateAttribute(Attribute attribute, double scaleFactor) {
		if(!checker.check(attribute.condition())) {
			return;
		}

		var id = attribute.id();
		var value = scaleFactor * attribute.getScaledValue(scalingParams);

		accumulateAttribute(id, value);
	}

	public abstract void accumulateAttribute(AttributeId id, double value);

	public void accumulateAttribute(AttributeId id, double value, AttributeCondition condition) {
		if (checker.check(condition)) {
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

package wow.character.model.snapshot;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeScalingParams;
import wow.commons.model.effect.component.StatConversion;

import java.util.List;
import java.util.function.Predicate;

/**
 * User: POlszewski
 * Date: 2023-10-16
 */
public abstract class AccumulatedStats {
	protected final AttributeScalingParams scalingParams;

	protected AccumulatedStats(AttributeScalingParams scalingParams) {
		this.scalingParams = scalingParams;
	}

	public void accumulateAttributes(List<Attribute> attributes, double scaleFactor, Predicate<Attribute> targetPredicate) {
		for (var attribute : attributes) {
			if (targetPredicate.test(attribute)) {
				accumulateAttribute(attribute, scaleFactor);
			}
		}
	}

	protected abstract void accumulateAttribute(Attribute attribute, double scaleFactor);

	public void solveStatConversions(List<StatConversion> statConversions) {
		for (var statConversion : statConversions) {
			if (statConversion.isFromOwner() && toConditionMatches(statConversion)) {
				accumulateConvertedStat(statConversion);
			}
		}
	}

	protected abstract boolean toConditionMatches(StatConversion statConversion);

	public void accumulateConvertedStat(StatConversion statConversion) {
		// void
	}
}

package wow.character.model.snapshot;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.AttributeScalingParams;
import wow.commons.model.attribute.AttributeTarget;
import wow.commons.model.effect.component.StatConversion;

import java.util.List;

import static wow.commons.model.attribute.AttributeTarget.OWNER;
import static wow.commons.model.attribute.AttributeTarget.PET;

/**
 * User: POlszewski
 * Date: 2023-10-16
 */
public abstract class AccumulatedStats {
	protected final AttributeScalingParams scalingParams;

	protected AccumulatedStats(AttributeScalingParams scalingParams) {
		this.scalingParams = scalingParams;
	}

	public void accumulateAttributes(List<Attribute> attributes, double scaleFactor) {
		for (var attribute : attributes) {
			if (isMatchingTarget(attribute)) {
				accumulateAttribute(attribute, scaleFactor);
			}
		}
	}

	private static boolean isMatchingTarget(Attribute attribute) {
		return attribute.target() != PET;
	}

	protected abstract void accumulateAttribute(Attribute attribute, double scaleFactor);

	public void solveStatConversions(List<StatConversion> statConversions, BaseStatsSnapshot baseStats) {
		for (var statConversion : statConversions) {
			accumulateConvertedStat(statConversion, baseStats);
		}
	}

	protected abstract void accumulateConvertedStat(StatConversion statConversion, BaseStatsSnapshot baseStats);

	protected double getAccumulatedValue(AttributeTarget attributeTarget, AttributeId attributeId, BaseStatsSnapshot baseStats) {
		if (attributeTarget != OWNER) {
			return 0;
		}

		return switch (attributeId) {
			case STAMINA -> baseStats.getStamina();
			case INTELLECT -> baseStats.getIntellect();
			case SPIRIT -> baseStats.getSpirit();
			default -> throw new IllegalArgumentException();
		};
	}
}

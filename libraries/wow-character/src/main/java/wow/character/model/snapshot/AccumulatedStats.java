package wow.character.model.snapshot;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.AttributeTarget;
import wow.commons.model.effect.component.StatConversion;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-10-16
 */
public abstract class AccumulatedStats {
	protected final int characterLevel;

	protected AccumulatedStats(int characterLevel) {
		this.characterLevel = characterLevel;
	}

	public void accumulateAttributes(List<Attribute> attributes, double scaleFactor) {
		for (var attribute : attributes) {
			if (isMatchingTarget(attribute)) {
				accumulateAttribute(attribute, scaleFactor);
			}
		}
	}

	private static boolean isMatchingTarget(Attribute attribute) {
		return attribute.id().getTarget() != AttributeTarget.PET;
	}

	protected abstract void accumulateAttribute(Attribute attribute, double scaleFactor);

	public void solveStatConversions(List<StatConversion> statConversions, BaseStatsSnapshot baseStats) {
		for (var statConversion : statConversions) {
			accumulateConvertedStat(statConversion, baseStats);
		}
	}

	protected abstract void accumulateConvertedStat(StatConversion statConversion, BaseStatsSnapshot baseStats);

	protected double getAccumulatedValue(AttributeId attributeId, BaseStatsSnapshot baseStats) {
		return switch (attributeId) {
			case INTELLECT -> baseStats.getIntellect();
			case SPIRIT -> baseStats.getSpirit();
			case PET_STAMINA, PET_INTELLECT -> 0; // pets not supported at this moment
			default -> throw new IllegalArgumentException();
		};
	}
}

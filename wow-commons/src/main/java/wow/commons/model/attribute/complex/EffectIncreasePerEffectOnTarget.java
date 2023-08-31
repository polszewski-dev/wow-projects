package wow.commons.model.attribute.complex;

import wow.commons.model.Percent;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.talent.TalentTree;

import java.util.Objects;

import static wow.commons.util.PrimitiveAttributeFormatter.getConditionString;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public record EffectIncreasePerEffectOnTarget(
		TalentTree effectTree,
		Percent increasePerEffectPct,
		Percent maxIncreasePct,
		AttributeCondition condition
) implements ComplexAttribute {
	public EffectIncreasePerEffectOnTarget {
		Objects.requireNonNull(effectTree);
		Objects.requireNonNull(increasePerEffectPct);
		Objects.requireNonNull(maxIncreasePct);
		Objects.requireNonNull(condition);
	}

	@Override
	public ComplexAttributeId id() {
		return ComplexAttributeId.EFFECT_INCREASE_PER_EFFECT_ON_TARGET;
	}

	@Override
	public EffectIncreasePerEffectOnTarget attachCondition(AttributeCondition condition) {
		return new EffectIncreasePerEffectOnTarget(effectTree, increasePerEffectPct, maxIncreasePct, condition);
	}

	@Override
	public String toString() {
		return "(tree: %s, increasePerEffect: %s, maxIncrease: %s)".formatted(effectTree, increasePerEffectPct, maxIncreasePct) + getConditionString(condition);
	}
}

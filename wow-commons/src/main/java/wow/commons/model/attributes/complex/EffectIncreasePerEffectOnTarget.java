package wow.commons.model.attributes.complex;

import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.talents.TalentTree;

import static wow.commons.util.PrimitiveAttributeFormatter.getConditionString;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public class EffectIncreasePerEffectOnTarget extends ComplexAttribute {
	private final TalentTree effectTree;
	private final Percent increasePerEffectPct;
	private final Percent maxIncreasePct;

	public EffectIncreasePerEffectOnTarget(TalentTree effectTree, Percent increasePerEffectPct, Percent maxIncreasePct, AttributeCondition condition) {
		super(ComplexAttributeId.EFFECT_INCREASE_PER_EFFECT_ON_TARGET, condition);
		this.effectTree = effectTree;
		this.increasePerEffectPct = increasePerEffectPct;
		this.maxIncreasePct = maxIncreasePct;
	}

	public TalentTree getEffectTree() {
		return effectTree;
	}

	public Percent getIncreasePerEffectPct() {
		return increasePerEffectPct;
	}

	public Percent getMaxIncreasePct() {
		return maxIncreasePct;
	}

	@Override
	public EffectIncreasePerEffectOnTarget attachCondition(AttributeCondition condition) {
		return new EffectIncreasePerEffectOnTarget(effectTree, increasePerEffectPct, maxIncreasePct, condition);
	}

	@Override
	public String toString() {
		return String.format("(tree: %s, increasePerEffect: %s, maxIncrease: %s)%s", effectTree, increasePerEffectPct, maxIncreasePct, getConditionString(condition));
	}
}

package wow.commons.model.attributes.complex;

import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.ComplexAttribute;
import wow.commons.model.talents.TalentTree;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public class EffectIncreasePerEffectOnTarget extends ComplexAttribute {
	private final TalentTree effectTree;
	private final Percent increasePerEffectPct;
	private final Percent maxIncreasePct;

	public EffectIncreasePerEffectOnTarget(TalentTree effectTree, Percent increasePerEffectPct, Percent maxIncreasePct, AttributeCondition condition) {
		super(AttributeId.effectIncreasePerEffectOnTarget, condition);
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
	public ComplexAttribute attachCondition(AttributeCondition condition) {
		return new EffectIncreasePerEffectOnTarget(effectTree, increasePerEffectPct, maxIncreasePct, condition);
	}

	@Override
	public String toString() {
		return String.format("(tree: %s, increasePerEffect: %s, maxIncrease: %s)", effectTree, increasePerEffectPct, maxIncreasePct);
	}
}
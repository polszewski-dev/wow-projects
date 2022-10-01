package wow.commons.model.effects;

import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;

/**
 * User: POlszewski
 * Date: 2020-10-02
 */
public class EffectInfo implements AttributeSource {
	private final EffectId effectId;
	private final boolean friendly;
	private final Scope scope;
	private final int maxStacks;
	private final RemoveCondition removeCondition;
	private final OnApply onApply;
	private final Attributes attributes;
	private final boolean stackScaling;

	public EffectInfo(EffectId effectId, boolean friendly, Scope scope, int maxStacks, RemoveCondition removeCondition, OnApply onApply, Attributes attributes, boolean stackScaling) {
		this.effectId = effectId;
		this.friendly = friendly;
		this.scope = scope;
		this.maxStacks = maxStacks;
		this.removeCondition = removeCondition;
		this.onApply = onApply;
		this.stackScaling = stackScaling;
		this.attributes = attributes;
	}

	public EffectId getEffectId() {
		return effectId;
	}

	public boolean isFriendly() {
		return friendly;
	}

	public Scope getScope() {
		return scope;
	}

	public int getMaxStacks() {
		return maxStacks;
	}

	public RemoveCondition getRemoveCondition() {
		return removeCondition;
	}

	public OnApply getOnApply() {
		return onApply;
	}

	@Override
	public Attributes getAttributes() {
		return attributes;
	}

	@Override
	public boolean scalesWithStacks() {
		return stackScaling;
	}

	@Override
	public String toString() {
		return attributes.toString();
	}
}

package wow.commons.model.effects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;

/**
 * User: POlszewski
 * Date: 2020-10-02
 */
@AllArgsConstructor
@Getter
public class EffectInfo implements AttributeSource {
	private final EffectId effectId;
	private final boolean friendly;
	private final Scope scope;
	private final int maxStacks;
	private final RemoveCondition removeCondition;
	private final OnApply onApply;
	private final Attributes attributes;
	private final boolean stackScaling;

	@Override
	public boolean scalesWithStacks() {
		return stackScaling;
	}

	@Override
	public String toString() {
		return attributes.toString();
	}
}

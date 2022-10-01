package wow.commons.model.attributes.complex.modifiers;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;

/**
 * User: POlszewski
 * Date: 2022-01-18
 */
public class AttributeEquivalent implements AttributeModifier {
	private final Attributes attributes;

	public AttributeEquivalent(Attributes attributes) {
		this.attributes = attributes;
	}

	@Override
	public Attributes getAveragedAttributes(StatProvider statProvider) {
		return attributes;
	}

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public String toString() {
		return attributes.toString();
	}
}

package wow.commons.model.attributes.complex.modifiers;

import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;

/**
 * User: POlszewski
 * Date: 2022-01-13
 */
public interface AttributeModifier {
	Attributes getAveragedAttributes(StatProvider statProvider);

	int getPriority();

	default ProcEvent getEvent() {
		return null;
	}

	default Percent getChance() {
		return Percent.ZERO;
	}
}

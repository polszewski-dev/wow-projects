package wow.commons.repository.impl.parsers.setters;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.repository.impl.parsers.StatParser;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2022-01-18
 */
public class EquivalentStatSetter implements StatSetter {
	public static final EquivalentStatSetter INSTANCE = new EquivalentStatSetter();

	private EquivalentStatSetter() {}

	@Override
	public void set(AttributesBuilder itemStats, StatParser parser, int groupNo) {
		String line = parser.getString(groupNo);
		StatSetterParams params = parser.getParams();

		Integer amount = parser.evalParam(params.getSpecialAmount());
		Attributes attributes = new Attributes(params.getAttributeParser().getAttributes(amount));

		SpecialAbility equivalent = SpecialAbility.equivalent(attributes, line);
		itemStats.addAttribute(equivalent);
	}
}

package wow.commons.repository.impl.parsers.setters;

import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.repository.impl.parsers.StatParser;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class MiscStatSetter implements StatSetter{
	public static final MiscStatSetter INSTANCE = new MiscStatSetter();

	private MiscStatSetter() {}

	@Override
	public void set(AttributesBuilder itemStats, StatParser parser, int groupNo) {
		SpecialAbility miscAbility = SpecialAbility.misc(parser.getString(groupNo));
		itemStats.addAttribute(miscAbility);
	}
}

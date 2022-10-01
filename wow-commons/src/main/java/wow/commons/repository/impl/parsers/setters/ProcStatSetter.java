package wow.commons.repository.impl.parsers.setters;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.ComplexAttribute;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.modifiers.ProcEvent;
import wow.commons.repository.impl.parsers.StatParser;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class ProcStatSetter implements StatSetter {
	public static final ProcStatSetter INSTANCE = new ProcStatSetter();

	private ProcStatSetter() {}

	@Override
	public void set(AttributesBuilder itemStats, StatParser parser, int groupNo) {
		ComplexAttribute proc = getProc(parser, groupNo);
		itemStats.addAttribute(proc);
	}

	private ComplexAttribute getProc(StatParser parser, int groupNo) {
		String line = parser.getString(groupNo);
		StatSetterParams params = parser.getParams();

		if (params.getSpecialType() == null) {
			return SpecialAbility.misc(line);
		}

		ProcEvent event = ProcEvent.valueOf(params.getSpecialType().toUpperCase());
		Percent chance = parser.evalParamPercent(params.getSpecialProcChance());
		Integer amount = parser.evalParam(params.getSpecialAmount());
		Duration duration = Duration.seconds(parser.evalParam(params.getSpecialDuration()));
		Duration cooldown = Duration.seconds(parser.evalParam(params.getSpecialProcCd()));

		if (chance == null) {
			chance = Percent._100;
		}

		Attributes attributes = new Attributes(params.getAttributeParser().getAttributes(amount));

		return SpecialAbility.proc(event, chance, attributes, duration, cooldown, line);
	}
}

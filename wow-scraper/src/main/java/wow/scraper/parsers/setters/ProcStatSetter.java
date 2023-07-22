package wow.scraper.parsers.setters;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.special.ProcEventType;
import wow.commons.model.attributes.complex.special.SpecialAbility;
import wow.commons.util.AttributesBuilder;
import wow.scraper.parsers.stats.StatMatcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class ProcStatSetter implements StatSetter {
	private final int groupNo;

	public ProcStatSetter(int groupNo) {
		this.groupNo = groupNo;
	}

	@Override
	public void set(AttributesBuilder itemStats, StatMatcher matcher) {
		ComplexAttribute proc = getProc(matcher);
		itemStats.addAttribute(proc);
	}

	private ComplexAttribute getProc(StatMatcher matcher) {
		String line = matcher.getString(groupNo);

		if (matcher.getParamType() == null) {
			return SpecialAbility.misc(line);
		}

		ProcEventType event = ProcEventType.parse(matcher.getParamType());
		Percent chance = matcher.getParamProcChance();
		Duration duration = matcher.getParamDuration();
		Duration cooldown = matcher.getParamProcCooldown();
		Attributes attributes = matcher.getParamStats();

		if (chance == null) {
			chance = Percent._100;
		}

		return SpecialAbility.proc(event, chance, attributes, duration, cooldown, line);
	}
}

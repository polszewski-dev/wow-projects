package wow.scraper.parsers.setters;

import wow.commons.model.Duration;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.util.AttributesBuilder;
import wow.scraper.parsers.stats.StatMatcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class OnUseStatSetter implements StatSetter {
	private final int groupNo;

	public OnUseStatSetter(int groupNo) {
		this.groupNo = groupNo;
	}

	@Override
	public void set(AttributesBuilder itemStats, StatMatcher matcher) {
		ComplexAttribute onUseAbility = getOnUseAbility(matcher);
		itemStats.addAttribute(onUseAbility);
	}

	private ComplexAttribute getOnUseAbility(StatMatcher matcher) {
		String line = matcher.getString(groupNo);

		if (matcher.getParamType() == null) {
			return SpecialAbility.misc(line);
		}

		if (!"periodic_bonus".equals(matcher.getParamType())) {
			throw new IllegalArgumentException("OnUse has incorrect special type");
		}

		Duration duration = matcher.getParamDuration();
		Duration cooldown = matcher.getParamCooldown();
		Attributes attributes = matcher.getParamStats();

		return SpecialAbility.onUse(attributes, duration, cooldown, line);
	}
}

package wow.commons.repository.impl.parsers.setters;

import wow.commons.model.Duration;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.ComplexAttribute;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.repository.impl.parsers.StatParser;
import wow.commons.util.AttributesBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class OnUseStatSetter implements StatSetter {
	public static final OnUseStatSetter INSTANCE = new OnUseStatSetter();

	private OnUseStatSetter() {}

	@Override
	public void set(AttributesBuilder itemStats, StatParser parser, int groupNo) {
		ComplexAttribute onUseAbility = getOnUseAbility(parser, groupNo);
		itemStats.addAttribute(onUseAbility);
	}

	private ComplexAttribute getOnUseAbility(StatParser parser, int groupNo) {
		String line = parser.getString(groupNo);
		StatSetterParams params = parser.getParams();

		if (params.getSpecialType() == null) {
			return SpecialAbility.misc(line);
		}

		if (!"periodic_bonus".equals(params.getSpecialType())) {
			throw new IllegalArgumentException("OnUse has incorrect special type");
		}

		Integer amount = parser.evalParam(params.getSpecialAmount());
		Duration duration = Duration.seconds(parser.evalParam(params.getSpecialDuration()));
		Duration cooldown = Duration.seconds(parseCooldown(parser.evalParamString(params.getSpecialCd())));

		Attributes attributes = Attributes.of(params.getAttributeParser().getAttributes(amount));

		return SpecialAbility.onUse(attributes, duration, cooldown, line);
	}

	private Integer parseCooldown(String value) {
		int[] parsedValues;

		parsedValues = tryParseCooldown("^(\\d+) Mins$", value);
		if (parsedValues != null) {
			return 60 * parsedValues[0];
		}

		parsedValues = tryParseCooldown("^(\\d+) Min (\\d+) Secs", value);
		if (parsedValues != null) {
			return 60 * parsedValues[0] + parsedValues[1];
		}

		throw new IllegalArgumentException(value);
	}

	private int[] tryParseCooldown(String regex, String value) {
		Pattern pattern = Pattern.compile("^" + regex + "$");
		Matcher matcher = pattern.matcher(value);
		if (matcher.find()) {
			int[] result = new int[matcher.groupCount()];
			for (int i = 1; i <= matcher.groupCount(); ++i) {
				result[i - 1] = Integer.parseInt(matcher.group(i));
			}
			return result;
		}
		return null;
	}
}

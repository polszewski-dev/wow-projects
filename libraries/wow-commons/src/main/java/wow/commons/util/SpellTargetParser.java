package wow.commons.util;

import wow.commons.model.spell.SpellTarget;
import wow.commons.model.spell.SpellTargetType;
import wow.commons.util.condition.SpellTargetConditionParser;

import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2025-11-29
 */
public final class SpellTargetParser {
	public static SpellTarget parse(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		var matcher = PATTERN.matcher(value);

		if (!matcher.find()) {
			throw new IllegalArgumentException("Incorrect spell target format: " + value);
		}

		var type = SpellTargetType.parse(matcher.group(1));
		var condition = SpellTargetConditionParser.parseCondition(matcher.group(3));

		return SpellTarget.of(type, condition);
	}

	private static final Pattern PATTERN = Pattern.compile("(\\w+)(\\s*\\[(.+)])?");

	private SpellTargetParser() {}
}

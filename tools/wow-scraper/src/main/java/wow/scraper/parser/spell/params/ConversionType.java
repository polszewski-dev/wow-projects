package wow.scraper.parser.spell.params;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-09-18
 */
public enum ConversionType {
	SPELL,
	EFFECT;

	public static ConversionType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}

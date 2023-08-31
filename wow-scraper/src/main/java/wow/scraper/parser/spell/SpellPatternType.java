package wow.scraper.parser.spell;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-09-15
 */
@AllArgsConstructor
@Getter
public enum SpellPatternType {
	SPELL(0),
	EFFECT(0),
	TRIGGERED_SPELL(0),

	TRIGGER_1(0),
	TRIGGER_2(1),
	TRIGGER_3(2),
	TRIGGER_4(3),
	TRIGGER_5(4),
	TRIGGERED_SPELL_1(0),
	TRIGGERED_SPELL_2(1),
	TRIGGERED_SPELL_3(2),
	TRIGGERED_SPELL_4(3),
	TRIGGERED_SPELL_5(4),
	;

	private final int triggerIdx;

	public static SpellPatternType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}

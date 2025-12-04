package wow.commons.util;

import wow.commons.model.spell.SpellTarget;
import wow.commons.util.condition.SpellTargetConditionFormatter;

/**
 * User: POlszewski
 * Date: 2025-11-29
 */
public final class SpellTargetFormatter {
	public static String format(SpellTarget spellTarget) {
		if (spellTarget == null) {
			return null;
		}

		var typeStr = spellTarget.type().toString().toLowerCase();

		if (spellTarget.condition().isEmpty()) {
			return typeStr;
		}

		var conditionStr = SpellTargetConditionFormatter.formatCondition(spellTarget.condition());

		return "%s [%s]".formatted(typeStr, conditionStr);
	}

	private SpellTargetFormatter() {}
}

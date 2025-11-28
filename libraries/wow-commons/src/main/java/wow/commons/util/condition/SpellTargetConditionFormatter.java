package wow.commons.util.condition;

import wow.commons.model.spell.SpellTargetCondition;

import java.util.List;

import static wow.commons.model.spell.SpellTargetCondition.*;
import static wow.commons.util.condition.SpellTargetConditionParser.HAS_EFFECT_PREFIX;
import static wow.commons.util.condition.SpellTargetConditionParser.HEALTH_AT_MOST_PREFIX;

/**
 * User: POlszewski
 * Date: 2025-11-28
 */
public class SpellTargetConditionFormatter extends ConditionFormatter<SpellTargetCondition> {
	public static String formatCondition(SpellTargetCondition condition) {
		return new SpellTargetConditionFormatter().format(condition);
	}

	@Override
	protected String formatPrimitiveCondition(SpellTargetCondition condition) {
		return switch (condition) {
			case Empty() -> "";

			case Operator ignored ->
					throw new IllegalArgumentException();

			case HasEffect(var abilityId) ->
					HAS_EFFECT_PREFIX + abilityId;

			case HealthAtMostPct(var value) ->
					percentPrefix(HEALTH_AT_MOST_PREFIX, value);

			case IsCreatureType(var creatureType) ->
					creatureType.getName();
		};
	}

	@Override
	protected boolean isComma(SpellTargetCondition condition) {
		return condition instanceof Comma;
	}

	@Override
	protected List<SpellTargetCondition> getCommaConditions(SpellTargetCondition comma) {
		return ((Comma) comma).conditions();
	}
}

package wow.commons.util.condition;

import wow.commons.model.effect.component.StatConversionCondition;

import static wow.commons.model.effect.component.StatConversionCondition.*;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public class StatConversionConditionFormatter extends ConditionFormatter<StatConversionCondition> {
	public static String formatCondition(StatConversionCondition condition) {
		return new StatConversionConditionFormatter().format(condition);
	}

	@Override
	protected String formatPrimitiveCondition(StatConversionCondition condition) {
		return switch (condition) {
			case ActionTypeCondition(var actionType) ->
					actionType.getName();

			case EmptyCondition() ->
					"";

			case PowerTypeCondition(var powerType) ->
					powerType.getKey();
		};
	}
}

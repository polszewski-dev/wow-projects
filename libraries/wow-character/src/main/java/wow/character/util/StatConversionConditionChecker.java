package wow.character.util;

import wow.commons.model.effect.component.StatConversionCondition;

import static wow.commons.model.effect.component.StatConversionCondition.*;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public final class StatConversionConditionChecker {
	public static boolean check(StatConversionCondition condition, AttributeConditionArgs args) {
		return switch (condition) {
			case ActionTypeCondition(var actionType) ->
					args.getActionType() == actionType;
			case EmptyCondition() ->
					true;
			case PowerTypeCondition(var powerType) ->
					args.getPowerType() == powerType;
		};
	}

	private StatConversionConditionChecker() {}
}

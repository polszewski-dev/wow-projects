package wow.commons.util.condition;

import wow.commons.model.effect.component.AbsorptionCondition;

import static wow.commons.model.effect.component.AbsorptionCondition.*;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public class AbsorptionConditionFormatter extends ConditionFormatter<AbsorptionCondition> {
	public static String formatCondition(AbsorptionCondition condition) {
		return new AbsorptionConditionFormatter().format(condition);
	}

	@Override
	protected String formatPrimitiveCondition(AbsorptionCondition condition) {
		return switch (condition) {
			case ActionTypeCondition(var actionType) ->
					actionType.getName();

			case EmptyCondition() ->
					"";

			case PowerTypeCondition(var powerType) ->
					powerType.getKey();

			case SpellSchoolCondition(var spellSchool) ->
					spellSchool.getName();
		};
	}
}

package wow.commons.util.condition;

import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.component.AbsorptionCondition;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.SpellSchool;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public class AbsorptionConditionParser extends ConditionParser<AbsorptionCondition> {
	public static AbsorptionCondition parseCondition(String value) {
		return new AbsorptionConditionParser(value).parse();
	}

	private AbsorptionConditionParser(String value) {
		super(value);
	}

	@Override
	protected AbsorptionCondition getBasicCondition(String value) {
		var actionType = ActionType.tryParse(value);

		if (actionType != null) {
			return AbsorptionCondition.of(actionType);
		}

		var powerType = PowerType.tryParse(value);

		if (powerType != null) {
			return AbsorptionCondition.of(powerType);
		}

		var spellSchool = SpellSchool.tryParse(value);

		if (spellSchool != null) {
			return AbsorptionCondition.of(spellSchool);
		}

		throw new IllegalArgumentException("Unknown condition: " + value);
	}

	@Override
	protected AbsorptionCondition getEmptyCondition() {
		return AbsorptionCondition.EMPTY;
	}
}

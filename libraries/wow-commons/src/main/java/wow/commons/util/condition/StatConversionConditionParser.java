package wow.commons.util.condition;

import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.component.StatConversionCondition;
import wow.commons.model.spell.ActionType;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public class StatConversionConditionParser extends ConditionParser<StatConversionCondition> {
	public static StatConversionCondition parseCondition(String value) {
		return new StatConversionConditionParser(value).parse();
	}

	private StatConversionConditionParser(String value) {
		super(value);
	}

	@Override
	protected StatConversionCondition getBasicCondition(String value) {
		var actionType = ActionType.tryParse(value);

		if (actionType != null) {
			return StatConversionCondition.of(actionType);
		}

		var powerType = PowerType.tryParse(value);

		if (powerType != null) {
			return StatConversionCondition.of(powerType);
		}

		throw new IllegalArgumentException("Unknown condition: " + value);
	}

	@Override
	protected StatConversionCondition getEmptyCondition() {
		return StatConversionCondition.EMPTY;
	}
}

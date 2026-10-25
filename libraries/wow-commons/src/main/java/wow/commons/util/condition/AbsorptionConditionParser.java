package wow.commons.util.condition;

import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.component.AbsorptionCondition;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.SpellSchool;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public class AbsorptionConditionParser extends ConditionParser<AbsorptionCondition, Void> {
	public static AbsorptionCondition parseCondition(String value) {
		return new AbsorptionConditionParser(value).parse();
	}

	private AbsorptionConditionParser(String value) {
		super(value);
	}

	@Override
	protected AbsorptionCondition getBasicCondition(String value) {
		return basicConditionChain
				.tryParse(value)
				.orElseThrow(() -> new IllegalArgumentException("Unknown condition: " + value));
	}

	private final ParseChain<AbsorptionCondition> basicConditionChain = new ParseChain<>(
			parseAndTransform(ActionType::tryParse, AbsorptionCondition::of),
			parseAndTransform(PowerType::tryParse, AbsorptionCondition::of),
			parseAndTransform(SpellSchool::tryParse, AbsorptionCondition::of)
	);

	@Override
	protected AbsorptionCondition getEmptyCondition() {
		return AbsorptionCondition.EMPTY;
	}
}

package wow.commons.constant;

import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.component.StatConversionCondition;
import wow.commons.model.spell.ActionType;

/**
 * User: POlszewski
 * Date: 2025-10-07
 */
public interface StatConversionConditions {
	StatConversionCondition SPELL = StatConversionCondition.of(ActionType.SPELL);
	StatConversionCondition SPELL_DAMAGE = StatConversionCondition.of(PowerType.SPELL_DAMAGE);
}

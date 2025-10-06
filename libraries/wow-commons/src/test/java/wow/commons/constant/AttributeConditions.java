package wow.commons.constant;

import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.SpellSchool;

/**
 * User: POlszewski
 * Date: 2025-10-07
 */
public interface AttributeConditions {
	AttributeCondition PHYSICAL = AttributeCondition.of(ActionType.PHYSICAL);
	AttributeCondition SPELL = AttributeCondition.of(ActionType.SPELL);
	AttributeCondition SPELL_DAMAGE = AttributeCondition.of(PowerType.SPELL_DAMAGE);
	AttributeCondition HEALING = AttributeCondition.of(PowerType.HEALING);
	AttributeCondition MELEE = AttributeCondition.of(PowerType.MELEE);
	AttributeCondition SHADOW = AttributeCondition.of(SpellSchool.SHADOW);
	AttributeCondition FROST = AttributeCondition.of(SpellSchool.FROST);
	AttributeCondition FIRE = AttributeCondition.of(SpellSchool.FIRE);
	AttributeCondition ARCANE = AttributeCondition.of(SpellSchool.ARCANE);
	AttributeCondition CURSE_OF_DOOM = AttributeCondition.of(AbilityIds.CURSE_OF_DOOM);
	AttributeCondition CURSE_OF_AGONY = AttributeCondition.of(AbilityIds.CURSE_OF_AGONY);
	AttributeCondition CURSE_OF_EXHAUSTION = AttributeCondition.of(AbilityIds.CURSE_OF_EXHAUSTION);
	AttributeCondition CORRUPTION = AttributeCondition.of(AbilityIds.CORRUPTION);
	AttributeCondition IMMOLATE = AttributeCondition.of(AbilityIds.IMMOLATE);
	AttributeCondition SHADOW_BOLT = AttributeCondition.of(AbilityIds.SHADOW_BOLT);
	AttributeCondition DRAIN_LIFE = AttributeCondition.of(AbilityIds.DRAIN_LIFE);
	AttributeCondition SOUL_FIRE = AttributeCondition.of(AbilityIds.SOUL_FIRE);
	AttributeCondition FLAMESTRIKE = AttributeCondition.of(AbilityIds.FLAMESTRIKE);
}

package wow.commons.constant;

import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.component.EventCondition;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.SpellSchool;

/**
 * User: POlszewski
 * Date: 2025-10-07
 */
public interface EventConditions {
	EventCondition PHYSICAL = EventCondition.of(ActionType.PHYSICAL);
	EventCondition SPELL = EventCondition.of(ActionType.SPELL);
	EventCondition SPELL_DAMAGE = EventCondition.of(PowerType.SPELL_DAMAGE);
	EventCondition HEALING = EventCondition.of(PowerType.HEALING);
	EventCondition MELEE = EventCondition.of(PowerType.MELEE);
	EventCondition SHADOW = EventCondition.of(SpellSchool.SHADOW);
	EventCondition FROST = EventCondition.of(SpellSchool.FROST);
	EventCondition FIRE = EventCondition.of(SpellSchool.FIRE);
	EventCondition ARCANE = EventCondition.of(SpellSchool.ARCANE);
	EventCondition CURSE_OF_DOOM = EventCondition.of(AbilityIds.CURSE_OF_DOOM);
	EventCondition CURSE_OF_AGONY = EventCondition.of(AbilityIds.CURSE_OF_AGONY);
	EventCondition CURSE_OF_EXHAUSTION = EventCondition.of(AbilityIds.CURSE_OF_EXHAUSTION);
	EventCondition CORRUPTION = EventCondition.of(AbilityIds.CORRUPTION);
	EventCondition IMMOLATE = EventCondition.of(AbilityIds.IMMOLATE);
	EventCondition SHADOW_BOLT = EventCondition.of(AbilityIds.SHADOW_BOLT);
	EventCondition DRAIN_LIFE = EventCondition.of(AbilityIds.DRAIN_LIFE);
	EventCondition SOUL_FIRE = EventCondition.of(AbilityIds.SOUL_FIRE);
	EventCondition FLAMESTRIKE = EventCondition.of(AbilityIds.FLAMESTRIKE);
}

package wow.character.util;

import wow.character.model.character.Character;
import wow.commons.model.Percent;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.attribute.condition.AttributeConditionArgs;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.DirectComponent;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2024-04-22
 */
public final class AttributeConditionArgsUtil {
	public static AttributeConditionArgs getCommonSpellConditionArgs(Character character, Spell spell) {
		var conditionArgs = new AttributeConditionArgs(ActionType.SPELL);

		if (spell instanceof Ability ability) {
			setAbilityConditionArgs(conditionArgs, ability);
		}

		conditionArgs.setSpellSchool(spell.getSchool());
		conditionArgs.setHasDamagingComponent(spell.hasDamagingComponent());
		conditionArgs.setHasHealingComponent(spell.hasHealingComponent());

		if (spell.getAppliedEffect() != null) {
			conditionArgs.setEffectCategory(spell.getAppliedEffect().getCategory());
		}

		conditionArgs.setOwnerHealth(Percent._100);

		if (character != null) {
			conditionArgs.setPetType(character.getActivePetType());
		}

		return conditionArgs;
	}

	public static AttributeConditionArgs getDamagingComponentConditionArgs(Character character, Spell spell, Character target, SpellSchool school) {
		var conditionArgs = getCommonSpellConditionArgs(character, spell);

		conditionArgs.setSpellSchool(school);
		conditionArgs.setPowerType(PowerType.SPELL_DAMAGE);
		conditionArgs.setHostileSpell(true);
		conditionArgs.setTargetingOthers(true);
		conditionArgs.setTargetType(target.getCreatureType());
		conditionArgs.setTargetClass(target.getCharacterClassId());
		conditionArgs.setTargetHealth(Percent._100);
		conditionArgs.setOwnerEffects(Set.of());

		return conditionArgs;
	}

	public static AttributeConditionArgs getDirectComponentConditionArgs(Character character, Spell spell, Character target, DirectComponent directComponent) {
		var conditionArgs = getDamagingComponentConditionArgs(character, spell, target, directComponent.school());

		conditionArgs.setDirect(true);
		conditionArgs.setCanCrit(true);
		conditionArgs.setHadCrit(false);
		return conditionArgs;
	}

	public static AttributeConditionArgs getPeriodicComponentConditionArgs(Character character, Spell spell, Character target, PeriodicComponent periodicComponent) {
		var conditionArgs = getDamagingComponentConditionArgs(character, spell, target, periodicComponent.school());

		conditionArgs.setPeriodic(true);
		conditionArgs.setCanCrit(false);
		conditionArgs.setHadCrit(false);
		return conditionArgs;
	}

	public static AttributeConditionArgs getTargetConditionArgs(Character target, Spell spell, PowerType powerType, SpellSchool school) {
		var conditionArgs = getCommonSpellConditionArgs(target, spell);

		conditionArgs.setSpellSchool(school);
		conditionArgs.setPowerType(powerType);
		return conditionArgs;
	}

	private static void setAbilityConditionArgs(AttributeConditionArgs conditionArgs, Ability ability) {
		if (ability instanceof ClassAbility classAbility) {
			conditionArgs.setTalentTree(classAbility.getTalentTree());
		}

		conditionArgs.setAbilityId(ability.getAbilityId());
		conditionArgs.setAbilityCategory(ability.getCategory());
		conditionArgs.setBaseCastTime(ability.getCastTime());

		var cost = ability.getCost();

		if (cost != null) {
			conditionArgs.setHasManaCost(cost.resourceType() == ResourceType.MANA);
		}
	}

	private AttributeConditionArgsUtil() {}
}

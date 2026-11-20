package wow.character.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.component.EventCondition;
import wow.commons.model.spell.*;

import java.util.Objects;

import static wow.commons.model.effect.component.EventCondition.*;
import static wow.commons.model.spell.ResourceType.MANA;


/**
 * User: POlszewski
 * Date: 29.09.2024
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Setter
public final class EventConditionChecker {
	private final Character caster;
	private final Spell spell;
	private final Character target;

	private final ActionType actionType;
	private PowerType powerType;

	private boolean canCrit;
	private boolean hadCrit;

	private boolean direct;
	private boolean periodic;

	private SpellSchool spellSchool;

	private boolean hostileSpell;
	private boolean normalMeleeAttack;
	private boolean specialAttack;

	private Character effectOwner;

	public static EventConditionChecker forSpell(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);
		return new EventConditionChecker(caster, spell, target, ActionType.SPELL);
	}

	public boolean check(EventCondition condition) {
		return switch (condition) {
			case AbilityCategoryCondition(var abilityCategory) ->
					spell instanceof Ability a &&
					a.getCategory() == abilityCategory;

			case AbilityIdCondition(var abilityId) ->
					spell instanceof Ability a &&
					a.getAbilityId().equals(abilityId);

			case ActionTypeCondition(var actionTypeArg) ->
					actionType == actionTypeArg;

			case CanCrit() ->
					canCrit;

			case EmptyCondition() ->
					true;

			case HadNoCrit() ->
					canCrit && !hadCrit;

			case HadCrit() ->
					canCrit && hadCrit;

			case HasCastTime() ->
					spell instanceof Ability a &&
					a.getCastTime().isPositive();

			case HasCastTimeUnder10Sec() ->
					spell instanceof Ability a &&
					a.getCastTime().getSeconds() > 0 &&
					a.getCastTime().getSeconds() < 10;

			case HasManaCost() ->
					spell instanceof Ability a &&
					a.getCost() != null &&
					a.getCost().resourceType() == MANA;

			case HasXFaction(var factionName) ->
					caster.hasExclusiveFaction(factionName);

			case IsDirect() ->
					direct;

			case IsHostileSpell() ->
					hostileSpell;

			case IsNormalMeleeAttack() ->
					normalMeleeAttack;

			case IsPeriodic() ->
					periodic;

			case IsSpecialAttack() ->
					specialAttack;

			case IsTargetingOthers() ->
					target != null && target != caster;

			case CasterIsOwner() ->
					caster == effectOwner;

			case Operator operator ->
					checkConditionOperator(operator);

			case OwnerHasEffectCondition(var effectName) ->
					caster.hasEffect(effectName);

			case OwnerHealthPctLessThan(var value) ->
					caster.getHealthPct().value() < value;

			case OwnerIsChannelingCondition(var abilityId) ->
					spell instanceof Ability a &&
					a.isChanneled() &&
					a.getAbilityId().equals(abilityId);

			case PowerTypeCondition(var powerTypeArg) ->
					powerType == powerTypeArg;

			case SpellSchoolCondition(var spellSchoolArg) ->
					getSpellSchool() == spellSchoolArg;

			case TalentTreeCondition(var talentTree) ->
					spell instanceof ClassAbility a &&
					a.getTalentTree() == talentTree;

			case TargetHealthPctLessThan(var value) ->
					target != null &&
					target.getHealthPct().value() < value;

			case TargetTypeCondition(var creatureType) ->
					target != null &&
					target.getCreatureType() == creatureType;
		};
	}

	private boolean checkConditionOperator(Operator operator) {
		return switch (operator) {
			case And(var left, var right) ->
					check(left) && check(right);

			case Or(var left, var right) ->
					check(left) || check(right);

			case Comma comma ->
					checkComma(comma);
		};
	}

	private boolean checkComma(Comma comma) {
		for (var condition : comma.conditions()) {
			if (check(condition)) {
				return true;
			}
		}
		return false;
	}

	private SpellSchool getSpellSchool() {
		if (spellSchool != null) {
			return spellSchool;
		}
		if (spell != null) {
			return spell.getSchool();
		}
		return null;
	}
}

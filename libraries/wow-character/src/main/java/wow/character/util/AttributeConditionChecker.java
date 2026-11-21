package wow.character.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeScalingParams;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.*;
import wow.commons.model.talent.TalentTree;

import static wow.commons.model.attribute.AttributeCondition.*;


/**
 * User: POlszewski
 * Date: 29.09.2024
 */
@RequiredArgsConstructor
@Getter
@Setter
public final class AttributeConditionChecker implements AttributeScalingParams {
	private final Character caster;
	private final Spell spell;
	private final Character target;

	private final ActionType actionType;
	private final PowerType powerType;
	private final SpellSchool spellSchool;

	private boolean direct;
	private boolean periodic;

	private Effect appliedEffect;

	private WeaponSubType weaponType;
	private boolean hadCrit;

	public AttributeConditionChecker withCaster(Character caster) {
		var result = new AttributeConditionChecker(caster, spell, target, actionType, powerType, spellSchool);

		result.direct = direct;
		result.periodic = periodic;
		result.appliedEffect = appliedEffect;
		result.weaponType = weaponType;
		result.hadCrit = hadCrit;

		return result;
	}

	public boolean check(AttributeCondition condition) {
		return switch (condition) {
			case AbilityCategoryCondition(var abilityCategory) ->
					spell instanceof Ability a &&
					a.getCategory() == abilityCategory;

			case AbilityIdCondition(var abilityId) ->
					spell instanceof Ability a &&
					a.getAbilityId().equals(abilityId);

			case ActionTypeCondition(var actionTypeArg) ->
					actionType == actionTypeArg;

			case EffectCategoryCondition(var effectCategory) ->
					appliedEffect != null &&
					appliedEffect.getCategory() == effectCategory;

			case EffectNameCondition(var effectName) ->
					appliedEffect != null &&
					appliedEffect.getName().equals(effectName);

			case EmptyCondition() ->
					true;

			case HadCrit() ->
					hadCrit;

			case HasCastTime() ->
					spell instanceof Ability a &&
					a.getCastTime().isPositive();

			case HasCastTimeUnder10Sec() ->
					spell instanceof Ability a &&
					a.getCastTime().getSeconds() < 10;

			case HasHealingComponent() ->
					spell != null &&
					spell.hasHealingComponent();

			case HasPet() ->
					caster.getActivePetType() != null;

			case IsDirect() ->
					direct;

			case IsPeriodic() ->
					periodic;

			case IsInstantCast() ->
					spell instanceof Ability a &&
					a.getCastTime().isZero();

			case MovementTypeCondition(var movementType) ->
					caster.getMovementType() == movementType;

			case Operator operator ->
					checkConditionOperator(operator);

			case OwnerHasEffect(var effectName) ->
					caster.hasEffect(effectName);

			case PetTypeCondition(var petType) -> caster.getMaster().getActivePetType() == petType;

			case PowerTypeCondition(var powerTypeArg) ->
					powerType == powerTypeArg;

			case ProfessionCondition(var professionId) ->
					caster.hasProfession(professionId);

			case SpellSchoolCondition(var spellSchoolArg) ->
					getSpellSchool() == spellSchoolArg;

			case TargetHealthPctLessThan(var value) ->
					target != null &&
					target.getHealthPct().value() < value;

			case TalentTreeCondition(var talentTree) ->
					spell instanceof ClassAbility a &&
					a.getTalentTree() == talentTree;

			case TargetTypeCondition(var creatureType) ->
					target != null && target.getCreatureType() == creatureType;

			case WeaponTypeCondition(var weaponTypeArg) ->
					weaponType == weaponTypeArg;
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

	@Override
	public int getLevel() {
		return caster.getLevel();
	}

	@Override
	public int getNumberOfEffectsOnTarget(TalentTree tree) {
		if (target == null) {
			return 0;
		}
		return target.getNumberOfEffects(tree);
	}
}

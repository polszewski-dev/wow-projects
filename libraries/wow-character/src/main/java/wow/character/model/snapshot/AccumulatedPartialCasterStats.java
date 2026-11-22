package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.*;
import wow.commons.model.talent.TalentTree;

import static wow.commons.model.attribute.AttributeCondition.*;

/**
 * User: POlszewski
 * Date: 2026-11-22
 */
public abstract class AccumulatedPartialCasterStats extends AccumulatedPartialStats {
	protected final Character caster;
	protected final Spell spell;
	protected final Character target;

	@Getter
	protected final ActionType actionType;
	@Getter
	protected final PowerType powerType;
	protected final SpellSchool spellSchool;

	protected boolean direct;
	protected boolean periodic;

	protected Effect appliedEffect;

	protected AccumulatedPartialCasterStats(Character caster, Spell spell, Character target, ActionType actionType, PowerType powerType, SpellSchool spellSchool) {
		this.caster = caster;
		this.spell = spell;
		this.target = target;
		this.actionType = actionType;
		this.powerType = powerType;
		this.spellSchool = spellSchool != null ? spellSchool
				: spell != null ? spell.getSchool()
				: null;
	}

	protected AccumulatedPartialCasterStats(AccumulatedPartialCasterStats stats) {
		this(stats.caster, stats.spell, stats.target, stats.actionType, stats.powerType, stats.spellSchool);
		this.direct = stats.direct;
		this.periodic = stats.periodic;
		this.appliedEffect = stats.appliedEffect;
	}

	protected void copyFrom(AccumulatedPartialCasterStats stats) {
		this.direct = stats.direct;
		this.periodic = stats.periodic;
		this.appliedEffect = stats.appliedEffect;
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

	@Override
	protected boolean checkCondition(AttributeCondition condition) {
		return switch (condition) {
			case CasterCondition casterCondition ->
					checkCasterCondition(casterCondition);

			case Operator operator ->
					checkConditionOperator(operator);

			default ->
					unsupported(condition);
		};
	}

	private boolean checkCasterCondition(CasterCondition condition) {
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

			case OwnerHasEffect(var effectName) ->
					caster.hasEffect(effectName);

			case PetTypeCondition(var petType) ->
					caster.getMaster().getActivePetType() == petType;

			case PowerTypeCondition(var powerTypeArg) ->
					powerType == powerTypeArg;

			case SpellSchoolCondition(var spellSchoolArg) ->
					spellSchool == spellSchoolArg;

			case TargetHealthPctLessThan(var value) ->
					target != null &&
					target.getHealthPct().value() < value;

			case TalentTreeCondition(var talentTree) ->
					spell instanceof ClassAbility a &&
					a.getTalentTree() == talentTree;

			case TargetTypeCondition(var creatureType) ->
					target != null && target.getCreatureType() == creatureType;

			case WeaponTypeCondition ignored ->
					false;
		};
	}
}

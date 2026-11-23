package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;

import java.util.Objects;

import static wow.commons.model.attribute.AttributeCondition.*;
import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.spell.ActionType.SPELL;
import static wow.commons.model.spell.component.ComponentCommand.*;

/**
 * User: POlszewski
 * Date: 2026-11-22
 */
@Getter
public final class AccumulatedTargetStats extends AccumulatedPartialStats {
	private double amountTaken;
	private double amountTakenPct;
	private double powerTaken;
	private double critTakenPct;
	private double receivedEffectDuration;
	private double receivedEffectDurationPct;

	private final Character target;
	private final Spell spell;

	private final ActionType actionType;
	private final PowerType powerType;
	private final SpellSchool spellSchool;

	private final boolean isDamage;
	private final boolean isHealing;

	private Effect appliedEffect;

	private AccumulatedTargetStats(Character target, Spell spell, ActionType actionType, PowerType powerType, SpellSchool spellSchool) {
		this.target = target;
		this.spell = spell;
		this.actionType = actionType;
		this.powerType = powerType;
		this.spellSchool = spellSchool != null ? spellSchool
				: spell != null ? spell.getSchool()
				: null;
		this.isDamage = powerType != null && powerType != HEALING;
		this.isHealing = powerType == HEALING;
	}

	public static AccumulatedTargetStats forSpellTarget(Character target, Spell spell, PowerType powerType) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);

		return new AccumulatedTargetStats(target, spell, SPELL, powerType, null);
	}

	public static AccumulatedTargetStats forSpellTarget(Character target, Spell spell, PowerType powerType, DirectCommand command) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);

		return new AccumulatedTargetStats(target, spell, SPELL, powerType, command.school());
	}

	public static AccumulatedTargetStats forSpellTarget(Character target, Spell spell, PowerType powerType, PeriodicCommand command) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);

		return new AccumulatedTargetStats(target, spell, SPELL, powerType, command.school());
	}

	public static AccumulatedTargetStats forAnySpellTarget(Character target) {
		Objects.requireNonNull(target);

		return new AccumulatedTargetStats(target, null, SPELL, null, null);
	}

	public static AccumulatedTargetStats forSpellEffectTarget(Character target, Spell spell, ApplyEffect command) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);

		var stats = new AccumulatedTargetStats(target, spell, SPELL, null, null);

		stats.appliedEffect = command.effect();

		return stats;
	}

	private AccumulatedTargetStats(AccumulatedTargetStats stats) {
		this(stats.target, stats.spell, stats.actionType, stats.powerType, stats.spellSchool);
		this.appliedEffect = stats.appliedEffect;
		this.amountTaken = stats.amountTaken;
		this.amountTakenPct = stats.amountTakenPct;
		this.powerTaken = stats.powerTaken;
		this.critTakenPct = stats.critTakenPct;
		this.receivedEffectDuration = stats.receivedEffectDuration;
		this.receivedEffectDurationPct = stats.receivedEffectDurationPct;
	}

	public AccumulatedTargetStats copy() {
		return new AccumulatedTargetStats(this);
	}

	@Override
	public int getLevel() {
		return target.getLevel();
	}

	@Override
	public int getNumberOfEffectsOnTarget(TalentTree tree) {
		return target.getNumberOfEffects(tree);
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case DAMAGE_TAKEN:
				if (isDamage) {
					this.amountTaken += getValue(attribute, scaleFactor);
				}
				break;
			case DAMAGE_TAKEN_PCT:
				if (isDamage) {
					this.amountTakenPct += getValue(attribute, scaleFactor);
				}
				break;
			case HEALING_TAKEN:
				if (isHealing) {
					this.amountTaken += getValue(attribute, scaleFactor);
				}
				break;
			case HEALING_TAKEN_PCT:
				if (isHealing) {
					this.amountTakenPct += getValue(attribute, scaleFactor);
				}
				break;
			case POWER_TAKEN:
				this.powerTaken += getValue(attribute, scaleFactor);
				break;
			case CRIT_TAKEN_PCT:
				this.critTakenPct += getValue(attribute, scaleFactor);
				break;
			case RECEIVED_EFFECT_DURATION:
				this.receivedEffectDuration += getValue(attribute, scaleFactor);
				break;
			case RECEIVED_EFFECT_DURATION_PCT:
				this.receivedEffectDurationPct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	@Override
	protected boolean checkCondition(AttributeCondition condition) {
		return switch (condition) {
			case TargetCondition targetCondition ->
					checkTargetCondition(targetCondition);

			case Operator operator ->
					checkConditionOperator(operator);

			default ->
					unsupported(condition);
		};
	}
	
	private boolean checkTargetCondition(TargetCondition condition) {
		return switch (condition) {
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

			case PetTypeCondition(var petType) ->
					target.getMaster().getActivePetType() == petType;

			case PowerTypeCondition(var powerTypeArg) ->
					powerType == powerTypeArg;

			case SpellSchoolCondition(var spellSchoolArg) ->
					spellSchool == spellSchoolArg;
		};
	}
}

package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;

import static wow.commons.model.attribute.AttributeCondition.*;

/**
 * User: POlszewski
 * Date: 2026-11-22
 */
public abstract class AccumulatedPartialTargetStats extends AccumulatedPartialStats {
	protected final Character target;
	protected final Spell spell;

	@Getter
	protected final ActionType actionType;
	@Getter
	protected final PowerType powerType;
	protected final SpellSchool spellSchool;

	protected Effect appliedEffect;

	protected AccumulatedPartialTargetStats(Character target, Spell spell, ActionType actionType, PowerType powerType, SpellSchool spellSchool) {
		this.target = target;
		this.spell = spell;
		this.actionType = actionType;
		this.powerType = powerType;
		this.spellSchool = spellSchool != null ? spellSchool
				: spell != null ? spell.getSchool()
				: null;
	}

	protected AccumulatedPartialTargetStats(AccumulatedPartialTargetStats stats) {
		this(stats.target, stats.spell, stats.actionType, stats.powerType, stats.spellSchool);
		this.appliedEffect = stats.appliedEffect;
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
	
	protected boolean checkTargetCondition(TargetCondition condition) {
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

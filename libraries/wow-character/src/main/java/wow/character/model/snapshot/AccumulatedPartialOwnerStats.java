package wow.character.model.snapshot;

import wow.character.model.character.Character;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.talent.TalentTree;

import static wow.commons.model.attribute.AttributeCondition.EmptyCondition;
import static wow.commons.model.attribute.AttributeCondition.Operator;

/**
 * User: POlszewski
 * Date: 2026-11-22
 */
public abstract class AccumulatedPartialOwnerStats extends AccumulatedPartialStats {
	protected final Character owner;

	protected AccumulatedPartialOwnerStats(Character owner) {
		this.owner = owner;
	}

	protected AccumulatedPartialOwnerStats(AccumulatedPartialOwnerStats stats) {
		this(stats.owner);
	}

	@Override
	public int getLevel() {
		return owner.getLevel();
	}

	@Override
	public int getNumberOfEffectsOnTarget(TalentTree tree) {
		return 0;
	}

	@Override
	protected boolean checkCondition(AttributeCondition condition) {
		return switch (condition) {
			case EmptyCondition() ->
					true;

			case Operator operator ->
					checkConditionOperator(operator);

			default ->
					unsupported(condition);
		};
	}

	@Override
	public ActionType getActionType() {
		return null;
	}

	@Override
	public PowerType getPowerType() {
		return null;
	}
}

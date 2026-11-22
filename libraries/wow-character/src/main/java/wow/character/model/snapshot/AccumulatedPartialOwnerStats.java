package wow.character.model.snapshot;

import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.talent.TalentTree;

/**
 * User: POlszewski
 * Date: 2026-11-22
 */
public abstract class AccumulatedPartialOwnerStats extends AccumulatedPartialStats {
	protected final Character owner;

	protected AccumulatedPartialOwnerStats(Character owner, AttributeConditionChecker checker) {
		super(checker);
		this.owner = owner;
	}

	protected AccumulatedPartialOwnerStats(AccumulatedPartialOwnerStats stats) {
		super(stats);
		this.owner = stats.owner;
	}

	@Override
	public int getLevel() {
		return owner.getLevel();
	}

	@Override
	public int getNumberOfEffectsOnTarget(TalentTree tree) {
		return 0;
	}
}

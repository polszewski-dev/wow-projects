package wow.character.model.snapshot;

import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.TalentTree;

/**
 * User: POlszewski
 * Date: 2026-11-22
 */
public abstract class AccumulatedPartialTargetStats extends AccumulatedPartialStats {
	protected final Character target;
	protected final Spell spell;

	protected AccumulatedPartialTargetStats(Character target, Spell spell, AttributeConditionChecker checker) {
		super(checker);
		this.target = target;
		this.spell = spell;
	}

	protected AccumulatedPartialTargetStats(AccumulatedPartialTargetStats stats) {
		super(stats);
		this.target = stats.target;
		this.spell = stats.spell;
	}

	@Override
	public int getLevel() {
		return target.getLevel();
	}

	@Override
	public int getNumberOfEffectsOnTarget(TalentTree tree) {
		return target.getNumberOfEffects(tree);
	}
}

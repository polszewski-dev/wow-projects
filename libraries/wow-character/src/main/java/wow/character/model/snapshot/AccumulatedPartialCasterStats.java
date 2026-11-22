package wow.character.model.snapshot;

import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.TalentTree;

/**
 * User: POlszewski
 * Date: 2026-11-22
 */
public abstract class AccumulatedPartialCasterStats extends AccumulatedPartialStats {
	protected final Character caster;
	protected final Spell spell;
	protected final Character target;

	protected AccumulatedPartialCasterStats(Character caster, Spell spell, Character target, AttributeConditionChecker checker) {
		super(checker);
		this.caster = caster;
		this.spell = spell;
		this.target = target;
	}

	protected AccumulatedPartialCasterStats(AccumulatedPartialCasterStats stats) {
		super(stats);
		this.caster = stats.caster;
		this.spell = stats.spell;
		this.target = stats.target;
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

package wow.commons.model.spell;

import wow.commons.model.talent.TalentTree;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
public interface ClassAbility extends Ability {
	@Override
	default SpellType getType() {
		return SpellType.CLASS_ABILITY;
	}

	TalentTree getTalentTree();
}

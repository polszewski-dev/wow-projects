package wow.commons.model.spell;

import wow.commons.model.talent.TalentTree;

/**
 * User: POlszewski
 * Date: 2026-08-10
 */
public interface PetAbility extends Ability {
	@Override
	default SpellType getType() {
		return SpellType.PET_ABILITY;
	}

	TalentTree getTalentTree();
}

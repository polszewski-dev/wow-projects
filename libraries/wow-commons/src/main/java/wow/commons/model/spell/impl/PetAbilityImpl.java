package wow.commons.model.spell.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spell.Cost;
import wow.commons.model.spell.PetAbility;
import wow.commons.model.talent.TalentTree;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
@Getter
@Setter
public class PetAbilityImpl extends AbilityImpl implements PetAbility {
	private int rank;
	private TalentTree talentTree;
	private Cost cost;
}

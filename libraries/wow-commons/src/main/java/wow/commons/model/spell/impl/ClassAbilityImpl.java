package wow.commons.model.spell.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spell.ClassAbility;
import wow.commons.model.spell.Cost;
import wow.commons.model.talent.TalentTree;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
@Getter
@Setter
public class ClassAbilityImpl extends AbilityImpl implements ClassAbility {
	private int rank;
	private TalentTree talentTree;
	private Cost cost;
}

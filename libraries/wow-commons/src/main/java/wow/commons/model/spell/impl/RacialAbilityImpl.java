package wow.commons.model.spell.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spell.Cost;
import wow.commons.model.spell.RacialAbility;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
@Getter
@Setter
public class RacialAbilityImpl extends AbilityImpl implements RacialAbility {
	private Cost cost;
}

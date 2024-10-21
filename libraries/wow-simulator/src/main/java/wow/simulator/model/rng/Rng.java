package wow.simulator.model.rng;

import wow.commons.model.spell.AbilityId;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public interface Rng {
	boolean hitRoll(double chancePct, AbilityId abilityId);

	boolean critRoll(double chancePct, AbilityId abilityId);
}

package wow.simulator.model.rng;

import wow.commons.model.spells.SpellId;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public interface Rng {
	boolean hitRoll(double chance, SpellId spellId);

	boolean critRoll(double chance, SpellId spellId);
}

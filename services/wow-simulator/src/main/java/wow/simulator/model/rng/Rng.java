package wow.simulator.model.rng;

import wow.commons.model.Percent;
import wow.commons.model.effect.component.Event;
import wow.commons.model.spell.Spell;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public interface Rng {
	boolean hitRoll(double chancePct, Spell spell);

	boolean critRoll(double chancePct, Spell spell);

	boolean eventRoll(double chancePct, Event event);

	default boolean eventRoll(Percent chance, Event event) {
		return eventRoll(chance.value(), event);
	}
}

package wow.simulator.model.rng;

import wow.commons.model.effect.component.Event;
import wow.commons.model.spell.Spell;

import java.util.Random;

/**
 * User: POlszewski
 * Date: 2024-11-18
 */
public class RealRng implements Rng {
	@Override
	public boolean hitRoll(double chancePct, Spell spell) {
		return chancePct >= rollPct();
	}

	@Override
	public boolean critRoll(double chancePct, Spell spell) {
		return chancePct >= rollPct();
	}

	@Override
	public boolean eventRoll(double chancePct, Event event) {
		return chancePct >= rollPct();
	}

	private double rollPct() {
		var random = new Random(System.nanoTime());
		var resolution = 100;

		return random.nextInt(0, 100 * resolution + 1) / (double) resolution;
	}
}

package wow.simulator.model.rng;

import wow.commons.model.spell.Spell;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public class PredeterminedRng implements Rng {
	private final Map<Object, Double> hitLuck = new LinkedHashMap<>();
	private final Map<Object, Double> critLuck = new LinkedHashMap<>();

	private static final int MAX_LUCK = 100;

	@Override
	public boolean hitRoll(double chancePct, Spell spell) {
		double initialLuck = 20.0 + Math.abs(spell.getName().hashCode() % 20);
		return roll(chancePct, initialLuck, spell, hitLuck);
	}

	@Override
	public boolean critRoll(double chancePct, Spell spell) {
		return roll(chancePct, 0, spell, critLuck);
	}

	private boolean roll(double chance, double initialLuck, Object id, Map<Object, Double> luck) {
		assertChanceInRange(chance);

		double currentLuck = luck.getOrDefault(id, initialLuck);
		currentLuck += chance;

		if (currentLuck >= MAX_LUCK) {
			currentLuck -= MAX_LUCK;
			luck.put(id, currentLuck);
			return true;
		}

		luck.put(id, currentLuck);
		return false;
	}

	private static void assertChanceInRange(double chance) {
		if (!(0 <= chance && chance <= MAX_LUCK)) {
			throw new IllegalArgumentException("Invalid chance: " + chance);
		}
	}
}

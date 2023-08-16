package wow.simulator.model.rng;

import wow.commons.model.spells.SpellId;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public class PredeterminedRng implements Rng {
	private final Map<Object, Double> luck = new LinkedHashMap<>();

	@Override
	public boolean hitRoll(double chance, SpellId spellId) {
		return true;
	}

	@Override
	public boolean critRoll(double chance, SpellId spellId) {
		return roll(chance, 0, spellId);
	}

	private boolean roll(double chance, double initialLuck, Object id) {
		assertChanceInRange(chance);

		double currentLuck = luck.getOrDefault(id, initialLuck);
		currentLuck += chance;

		if (currentLuck >= 1) {
			currentLuck -= 1;
			luck.put(id, currentLuck);
			return true;
		}

		luck.put(id, currentLuck);
		return false;
	}

	private static void assertChanceInRange(double chance) {
		if (!(0 <= chance && chance <= 1)) {
			throw new IllegalArgumentException("Invalid chance: " + chance);
		}
	}
}

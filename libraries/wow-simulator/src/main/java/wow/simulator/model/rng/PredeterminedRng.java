package wow.simulator.model.rng;

import wow.commons.model.spell.AbilityId;

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
	public boolean hitRoll(double chancePct, AbilityId abilityId) {
		double initialLuck = 20.0 + Math.abs(abilityId.toString().hashCode() % 20);
		return roll(chancePct, initialLuck, abilityId, hitLuck);
	}

	@Override
	public boolean critRoll(double chancePct, AbilityId abilityId) {
		return roll(chancePct, 0, abilityId, critLuck);
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

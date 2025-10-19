package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2025-10-12
 */
@Getter
@Setter
public class RegenSnapshot {
	private int uninterruptedManaRegen;
	private int interruptedManaRegen;
	private int outOfCombatHealthRegen;
	private int inCombatHealthRegen;
	private double healthGeneratedPct;

	public int getHealthToRegen(boolean inCombat, Duration sinceLastRegen) {
		var healthRegen = getHealthRegen(inCombat);
		var health = healthRegen * sinceLastRegen.getSeconds() / SCALING_FACTOR;

		return (int) health;
	}

	public int getManaToRegen(Duration sinceLastManaSpent, Duration sinceLastRegen) {
		var manaRegen = getManaRegen(sinceLastManaSpent);
		var mana = manaRegen * sinceLastRegen.getSeconds() / SCALING_FACTOR;

		return (int) mana;
	}

	private int getHealthRegen(boolean inCombat) {
		if (inCombat) {
			return inCombatHealthRegen;
		}

		return outOfCombatHealthRegen;
	}

	private int getManaRegen(Duration sinceLastManaSpent) {
		if (sinceLastManaSpent == null || sinceLastManaSpent.compareTo(SUPPRESSED_MANA_DURATION) >= 0) {
			return uninterruptedManaRegen;
		}

		return interruptedManaRegen;
	}

	private static final Duration SUPPRESSED_MANA_DURATION = Duration.seconds(5);
	private static final double SCALING_FACTOR = 5.0;
}

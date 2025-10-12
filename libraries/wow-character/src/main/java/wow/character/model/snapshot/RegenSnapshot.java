package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;

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
}

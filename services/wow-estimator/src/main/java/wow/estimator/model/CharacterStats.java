package wow.estimator.model;

import wow.character.model.snapshot.StatSummary;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record CharacterStats(
		StatSummary current,
		StatSummary equipment,
		StatSummary nofBuffs,
		StatSummary selfBuffs,
		StatSummary partyBuffs,
		StatSummary partyBuffsAndConsumes,
		StatSummary raidBuffsAndConsumes,
		StatSummary worldBuffsAndConsumes
) {
}

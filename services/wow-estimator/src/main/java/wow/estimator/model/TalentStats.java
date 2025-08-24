package wow.estimator.model;

import wow.commons.model.talent.Talent;

/**
 * User: POlszewski
 * Date: 2024-03-28
 */
public record TalentStats(
		Talent talent,
		String statEquivalent,
		double spEquivalent
) {
}

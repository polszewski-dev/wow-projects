package wow.commons.model.sources;

import wow.commons.model.pve.Faction;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public record ReputationReward(Faction faction) implements Source {
	@Override
	public boolean isReputationReward() {
		return true;
	}

	@Override
	public String toString() {
		return "Reputation: " + faction;
	}
}

package wow.commons.model.sources;

import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Phase;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class ReputationReward extends NotSourcedFromInstance {
	private final Faction faction;

	ReputationReward(Faction faction, Phase phase) {
		super(phase != null ? phase : faction.getPhase());
		this.faction = faction;
	}

	@Override
	public Faction getFaction() {
		return faction;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ReputationReward that = (ReputationReward) o;
		return faction == that.faction;
	}

	@Override
	public int hashCode() {
		return Objects.hash(faction);
	}

	@Override
	public String toString() {
		return "Reputation: " + faction;
	}
}

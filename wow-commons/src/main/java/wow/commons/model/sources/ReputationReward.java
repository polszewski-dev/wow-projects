package wow.commons.model.sources;

import wow.commons.model.pve.Faction;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class ReputationReward extends NotSourcedFromInstance {
	private final Faction faction;

	ReputationReward(Faction faction, Integer phase) {
		super(phase);
		this.faction = faction;
	}

	@Override
	protected int getDefaultPhase() {
		return faction.getPhase();
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

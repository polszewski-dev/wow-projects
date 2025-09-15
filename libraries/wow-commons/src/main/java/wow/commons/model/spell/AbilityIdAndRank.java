package wow.commons.model.spell;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record AbilityIdAndRank(AbilityId abilityId, int rank) {
	public AbilityIdAndRank {
		Objects.requireNonNull(abilityId);
	}

	@Override
	public String toString() {
		if (rank == 0) {
			return abilityId.name();
		}
		return String.format("%s (Rank %s)", abilityId.name(), rank);
	}
}

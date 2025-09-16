package wow.commons.model.spell;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record AbilityNameRank(String name, int rank) {
	public AbilityNameRank {
		Objects.requireNonNull(name);
	}

	@Override
	public String toString() {
		if (rank == 0) {
			return name;
		}
		return String.format("%s (Rank %s)", name, rank);
	}
}

package wow.commons.model.talent;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record TalentNameRank(String name, int rank) {
	public TalentNameRank {
		Objects.requireNonNull(name);
	}

	@Override
	public String toString() {
		return String.format("%s (Rank %s)", name, rank);
	}
}

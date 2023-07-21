package wow.commons.model.talents;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record TalentIdAndRank(TalentId talentId, int rank) {
	public TalentIdAndRank {
		Objects.requireNonNull(talentId);
	}

	@Override
	public String toString() {
		return String.format("%s (Rank %s)", talentId.getName(), rank);
	}
}

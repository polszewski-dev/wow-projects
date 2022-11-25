package wow.commons.model.talents;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class TalentIdAndRank {
	@NonNull
	private final TalentId talentId;
	private final int rank;

	@Override
	public String toString() {
		return String.format("%s (Rank %s)", talentId.getName(), rank);
	}
}

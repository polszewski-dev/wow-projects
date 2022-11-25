package wow.commons.model.spells;

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
public class SpellIdAndRank {
	@NonNull
	private final SpellId spellId;
	private final Integer rank;

	@Override
	public String toString() {
		if (rank == null) {
			return spellId.getName();
		}
		return String.format("%s (Rank %s)", spellId.getName(), rank);
	}
}

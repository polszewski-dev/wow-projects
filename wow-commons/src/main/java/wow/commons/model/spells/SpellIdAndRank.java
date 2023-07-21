package wow.commons.model.spells;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record SpellIdAndRank(SpellId spellId, int rank) {
	public SpellIdAndRank {
		Objects.requireNonNull(spellId);
	}

	@Override
	public String toString() {
		if (rank == 0) {
			return spellId.getName();
		}
		return String.format("%s (Rank %s)", spellId.getName(), rank);
	}
}

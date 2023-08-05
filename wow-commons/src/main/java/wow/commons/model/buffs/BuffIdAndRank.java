package wow.commons.model.buffs;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-08-03
 */
public record BuffIdAndRank(BuffId buffId, int rank) implements Comparable<BuffIdAndRank> {
	public BuffIdAndRank {
		Objects.requireNonNull(buffId);
	}

	@Override
	public int compareTo(BuffIdAndRank other) {
		int cmp = this.buffId.compareTo(other.buffId);

		if (cmp == 0) {
			return cmp;
		}
		return Integer.compare(this.rank, other.rank);
	}

	@Override
	public String toString() {
		if (rank == 0) {
			return buffId.getName();
		}
		return String.format("%s (Rank %s)", buffId.getName(), rank);
	}
}

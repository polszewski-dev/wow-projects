package wow.commons.model.buff;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-08-03
 */
public record BuffNameRank(String name, int rank) implements Comparable<BuffNameRank> {
	public BuffNameRank {
		Objects.requireNonNull(name);
	}

	@Override
	public int compareTo(BuffNameRank other) {
		int cmp = this.name.compareTo(other.name);

		if (cmp == 0) {
			return cmp;
		}
		return Integer.compare(this.rank, other.rank);
	}

	@Override
	public String toString() {
		if (rank == 0) {
			return name;
		}
		return String.format("%s (Rank %s)", name, rank);
	}
}

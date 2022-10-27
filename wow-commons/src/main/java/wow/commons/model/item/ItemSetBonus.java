package wow.commons.model.item;

import wow.commons.model.attributes.Attributes;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public class ItemSetBonus {
	private final int numPieces;
	private final String description;
	private Attributes bonusStats;

	public ItemSetBonus(int numPieces, String description) {
		this.numPieces = numPieces;
		this.description = description;
	}

	public int getNumPieces() {
		return numPieces;
	}

	public String getDescription() {
		return description;
	}

	public Attributes getBonusStats() {
		return bonusStats;
	}

	public void setBonusStats(Attributes bonusStats) {
		this.bonusStats = bonusStats;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ItemSetBonus that = (ItemSetBonus) o;
		return numPieces == that.numPieces &&
				description.equals(that.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(numPieces, description);
	}

	@Override
	public String toString() {
		return String.format("(%s) Set: %s", numPieces, description);
	}
}

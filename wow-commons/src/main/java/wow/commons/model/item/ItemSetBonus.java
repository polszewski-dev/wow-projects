package wow.commons.model.item;

import wow.commons.model.attribute.Attributes;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public record ItemSetBonus(int numPieces, String description, Attributes bonusStats) {
	public ItemSetBonus {
		Objects.requireNonNull(description);
		Objects.requireNonNull(bonusStats);
	}

	@Override
	public String toString() {
		return String.format("(%s) Set: %s", numPieces, description);
	}
}

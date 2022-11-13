package wow.commons.model.item;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.attributes.Attributes;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = {"numPieces", "description"})
public class ItemSetBonus {
	private final int numPieces;
	private final String description;
	private Attributes bonusStats;

	@Override
	public String toString() {
		return String.format("(%s) Set: %s", numPieces, description);
	}
}

package wow.commons.model.item;

import wow.commons.model.attributes.complex.special.SpecialAbilitySource;
import wow.commons.model.config.Description;

import java.util.Comparator;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record ItemSetSource(ItemSet itemSet, int numPieces) implements SpecialAbilitySource, Comparable<ItemSetSource> {
	@Override
	public Description getDescription() {
		return new Description(
				"%s (%s)".formatted(itemSet.getName(), numPieces),
				getFirstItem().getDescription().icon(),
				null
		);
	}

	@Override
	public int getPriority() {
		return 1;
	}

	private Item getFirstItem() {
		return itemSet.getPieces().stream()
				.min(Comparator.comparing(Item::getItemType))
				.orElseThrow();
	}

	@Override
	public int compareTo(ItemSetSource o) {
		int cmp = this.itemSet.getName().compareTo(o.itemSet.getName());

		if (cmp != 0) {
			return cmp;
		}

		return this.numPieces - o.numPieces;
	}
}

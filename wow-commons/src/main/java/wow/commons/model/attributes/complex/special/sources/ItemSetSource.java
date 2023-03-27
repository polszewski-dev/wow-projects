package wow.commons.model.attributes.complex.special.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.config.Description;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSet;

import java.util.Comparator;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ItemSetSource implements SpecialAbilitySource, Comparable<ItemSetSource> {
	private final ItemSet itemSet;
	private final int numPieces;

	@Override
	public Description getDescription() {
		return new Description(
				String.format("%s (%s)", itemSet.getName(), numPieces),
				getFirstItem().getDescription().getIcon(),
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

package wow.commons.model.item;

import wow.commons.model.config.Description;
import wow.commons.model.effect.EffectSource;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record ItemSetSource(ItemSet itemSet, int numPieces) implements EffectSource, Comparable<ItemSetSource> {
	@Override
	public Description getDescription() {
		return new Description(
				"%s (%s)".formatted(itemSet.getName(), numPieces),
				itemSet.getIcon(),
				null
		);
	}

	@Override
	public int getPriority() {
		return 1;
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

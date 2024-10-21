package wow.commons.model.item;

import wow.commons.model.config.Description;
import wow.commons.model.effect.EffectSource;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record ItemSource(AbstractItem item) implements EffectSource, Comparable<ItemSource> {
	@Override
	public Description getDescription() {
		return item.getDescription();
	}

	@Override
	public int getPriority() {
		return 2;
	}

	@Override
	public int compareTo(ItemSource o) {
		return Integer.compare(this.item.getId(), o.item.getId());
	}
}

package wow.commons.model.item;

import wow.commons.model.attribute.complex.special.SpecialAbilitySource;
import wow.commons.model.config.Description;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record ItemSource(AbstractItem item) implements SpecialAbilitySource, Comparable<ItemSource> {
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
		return this.item.getId().compareTo(o.item.getId());
	}
}

package wow.commons.model.attributes.complex.special.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.config.Description;
import wow.commons.model.item.AbstractItem;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ItemSource implements SpecialAbilitySource, Comparable<ItemSource> {
	private final AbstractItem item;

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

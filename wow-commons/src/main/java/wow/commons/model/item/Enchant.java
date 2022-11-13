package wow.commons.model.item;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-05
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Enchant implements AttributeSource {
	private final int id;
	private final String name;
	private final List<ItemType> itemTypes;
	private final Attributes stats;

	@Override
	public Attributes getAttributes() {
		return stats;
	}

	public boolean matches(ItemType itemType) {
		return this.itemTypes.contains(itemType);
	}

	@Override
	public String toString() {
		return name + " " + stats;
	}
}

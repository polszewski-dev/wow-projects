package wow.commons.model.item;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.ConfigurationElementWithAttributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-05
 */
@Getter
public class Enchant extends ConfigurationElementWithAttributes<Integer> {
	private final List<ItemType> itemTypes;

	public Enchant(Integer id, Description description, Restriction restriction, Attributes attributes, List<ItemType> itemTypes) {
		super(id, description, restriction, attributes);
		this.itemTypes = itemTypes;
	}

	public boolean matches(ItemType itemType) {
		return this.itemTypes.contains(itemType);
	}
}

package wow.commons.model.item;

import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-05
 */
public class Enchant implements AttributeSource {
	private final int id;
	private final String name;
	private final List<ItemType> itemTypes;
	private final Attributes stats;

	public Enchant(int id, String name, List<ItemType> itemTypes, Attributes stats) {
		this.id = id;
		this.name = name;
		this.itemTypes = itemTypes;
		this.stats = stats;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<ItemType> getItemTypes() {
		return itemTypes;
	}

	@Override
	public Attributes getAttributes() {
		return stats;
	}

	public boolean matches(ItemType itemType) {
		return this.itemTypes.contains(itemType);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Enchant)) return false;
		Enchant enchant = (Enchant) o;
		return name.equals(enchant.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return name + " " + stats;
	}
}

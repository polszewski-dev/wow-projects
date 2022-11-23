package wow.commons.model.item;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.sources.Source;

import java.util.Objects;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
@Getter
public class TradedItem extends AbstractItem {
	public TradedItem(int itemId, String name, ItemType itemType, ItemRarity rarity, Set<Source> sources) {
		super(itemId, name, itemType, rarity, Attributes.EMPTY, sources);
		if (itemType != ItemType.QUEST && itemType != ItemType.TOKEN) {
			throw new IllegalArgumentException("Wrong item type: " + itemType);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TradedItem)) return false;
		TradedItem tradedItem = (TradedItem) o;
		return getId() == tradedItem.getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public String toString() {
		return getName();
	}
}

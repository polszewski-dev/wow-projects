package wow.commons.model.item;

import wow.commons.model.attribute.Attributes;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.PveRoleClassified;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface Item extends AbstractItem, PveRoleClassified {
	ItemSet getItemSet();

	ItemSocketSpecification getSocketSpecification();

	WeaponStats getWeaponStats();

	default int getSocketCount() {
		return getSocketSpecification().getSocketCount();
	}

	default boolean hasSockets() {
		return getSocketSpecification().hasSockets();
	}

	default SocketType getSocketType(int i) {
		return getSocketSpecification().getSocketType(i);
	}

	default List<SocketType> getSocketTypes() {
		return getSocketSpecification().socketTypes();
	}

	default Attributes getSocketBonus() {
		return getSocketSpecification().socketBonus();
	}

	default boolean canBeEquippedIn(ItemSlot itemSlot) {
		return getItemType().getItemSlots().contains(itemSlot);
	}
}

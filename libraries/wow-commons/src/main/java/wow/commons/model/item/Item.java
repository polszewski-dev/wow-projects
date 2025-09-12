package wow.commons.model.item;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.PveRoleClassified;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.ActivatedAbility;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface Item extends AbstractItem<ItemId>, PveRoleClassified {
	ItemSet getItemSet();

	ItemSocketSpecification getSocketSpecification();

	WeaponStats getWeaponStats();

	List<Effect> getEffects();

	ActivatedAbility getActivatedAbility();

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

	default Effect getSocketBonus() {
		return getSocketSpecification().socketBonus();
	}

	default boolean canBeEquippedIn(ItemSlot itemSlot) {
		return getItemType().getItemSlots().contains(itemSlot);
	}
}

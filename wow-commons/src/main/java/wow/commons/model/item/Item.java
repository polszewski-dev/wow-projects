package wow.commons.model.item;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterInfo;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@Getter
public class Item extends AbstractItem {
	private ItemSet itemSet;
	private final ItemSocketSpecification socketSpecification;
	private final WeaponStats weaponStats;

	public Item(
			Integer id,
			Description description,
			Restriction restriction,
			Attributes attributes,
			BasicItemInfo basicItemInfo,
			ItemSocketSpecification socketSpecification,
			WeaponStats weaponStats
	) {
		super(id, description, restriction, attributes, basicItemInfo);
		this.socketSpecification = socketSpecification;
		this.weaponStats = weaponStats;
	}

	public boolean isEnchantable() {
		return getItemType().isEnchantable(getItemSubType());
	}

	public int getSocketCount() {
		return socketSpecification.getSocketCount();
	}

	public boolean hasSockets() {
		return socketSpecification.hasSockets();
	}

	public SocketType getSocketType(int i) {
		return socketSpecification.getSocketType(i);
	}

	public List<SocketType> getSocketTypes() {
		return socketSpecification.getSocketTypes();
	}

	public Attributes getSocketBonus() {
		return socketSpecification.getSocketBonus();
	}

	public void setItemSet(ItemSet itemSet) {
		this.itemSet = itemSet;
	}

	public boolean canBeEquippedIn(ItemSlot itemSlot) {
		return getItemType().getItemSlots().contains(itemSlot);
	}

	@Override
	public boolean isAvailableTo(CharacterInfo characterInfo) {
		if (!getRestriction().isMetBy(characterInfo)) {
			return false;
		}
		return itemSet == null || itemSet.getRestriction().isMetBy(characterInfo);
	}
}

package wow.commons.model.categorization;

import lombok.Getter;
import wow.commons.util.EnumUtil;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@Getter
public enum ItemType {
	HEAD("Head", ItemCategory.ARMOR, ItemSlot.HEAD),
	NECK("Neck", ItemCategory.ACCESSORY, ItemSlot.NECK),
	SHOULDER("Shoulder", ItemCategory.ARMOR, ItemSlot.SHOULDER),
	BACK("Back", ItemCategory.ARMOR, ItemSlot.BACK),
	CHEST("Chest", ItemCategory.ARMOR, ItemSlot.CHEST),
	SHIRT("Shirt", ItemCategory.ARMOR, ItemSlot.SHIRT),
	TABARD("Tabard", ItemCategory.ARMOR, ItemSlot.TABARD),
	WRIST("Wrist", ItemCategory.ARMOR, ItemSlot.WRIST),
	HANDS("Hands", ItemCategory.ARMOR, ItemSlot.HANDS),
	WAIST("Waist", ItemCategory.ARMOR, ItemSlot.WAIST),
	LEGS("Legs", ItemCategory.ARMOR, ItemSlot.LEGS),
	FEET("Feet", ItemCategory.ARMOR, ItemSlot.FEET),
	FINGER("Finger", ItemCategory.ACCESSORY, ItemSlot.FINGER_1, ItemSlot.FINGER_2),
	TRINKET("Trinket", ItemCategory.ACCESSORY, ItemSlot.TRINKET_1, ItemSlot.TRINKET_2),

	TWO_HAND("Two-Hand", ItemCategory.WEAPON, ItemSlot.MAIN_HAND),
	ONE_HAND("One-Hand", ItemCategory.WEAPON, ItemSlot.MAIN_HAND, ItemSlot.OFF_HAND),
	MAIN_HAND("Main Hand", ItemCategory.WEAPON, ItemSlot.MAIN_HAND),
	OFF_HAND("Off Hand", ItemCategory.WEAPON, ItemSlot.OFF_HAND),

	RELIC("Relic", ItemCategory.WEAPON, ItemSlot.RANGED),
	RANGED("Ranged", ItemCategory.WEAPON, ItemSlot.RANGED),
	THROWN("Thrown", ItemCategory.WEAPON, ItemSlot.RANGED),

	ENCHANT("Enchant", ItemCategory.ENCHANT),
	GEM("Gem", ItemCategory.GEM),

	QUEST("This Item Begins a Quest", ItemCategory.QUEST),
	TOKEN("Token", ItemCategory.TOKEN),

	BAG("Bag", ItemCategory.CONTAINER),
	PROJECTILE("Projectile", ItemCategory.PROJECTILE),

	CONSUMABLE("Consumable", ItemCategory.CONSUMABLE),

	CRAFTING_MATERIAL("Crafting Material", ItemCategory.CRAFTING_MATERIAL),
	PATTERN("Pattern", ItemCategory.PATTERN);

	private final String key;
	private final ItemCategory category;
	private final List<ItemSlot> itemSlots;

	ItemType(String key, ItemCategory category, ItemSlot... slots) {
		this.key = key;
		this.category = category;
		this.itemSlots = List.of(slots);
	}

	public static ItemType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	public static ItemType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public ItemSlot getUniqueItemSlot() {
		if (itemSlots.size() != 1) {
			throw new IllegalArgumentException("Required unique item slot for: " + this);
		}
		return itemSlots.getFirst();
	}

	@Override
	public String toString() {
		return key;
	}
}

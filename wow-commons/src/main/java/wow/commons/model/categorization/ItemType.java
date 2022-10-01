package wow.commons.model.categorization;

import java.util.List;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public enum ItemType {
	Head("Head", ItemCategory.Armor, ItemSlot.Head),
	Neck("Neck", ItemCategory.Accessory, ItemSlot.Neck),
	Shoulder("Shoulder", ItemCategory.Armor, ItemSlot.Shoulder),
	Back("Back", ItemCategory.Armor, ItemSlot.Back),
	Chest("Chest", ItemCategory.Armor, ItemSlot.Chest),
	Shirt("Shirt", ItemCategory.Armor, ItemSlot.Shirt),
	Tabard("Tabard", ItemCategory.Armor, ItemSlot.Tabard),
	Wrist("Wrist", ItemCategory.Armor, ItemSlot.Wrist),
	Hands("Hands", ItemCategory.Armor, ItemSlot.Hands),
	Waist("Waist", ItemCategory.Armor, ItemSlot.Waist),
	Legs("Legs", ItemCategory.Armor, ItemSlot.Legs),
	Feet("Feet", ItemCategory.Armor, ItemSlot.Feet),
	Finger("Finger", ItemCategory.Accessory, ItemSlot.Finger1, ItemSlot.Finger2),
	Trinket("Trinket", ItemCategory.Accessory, ItemSlot.Trinket1, ItemSlot.Trinket2),

	TwoHand("Two-Hand", ItemCategory.Weapon, ItemSlot.MainHand),
	OneHand("One-Hand", ItemCategory.Weapon, ItemSlot.MainHand, ItemSlot.OffHand),
	MainHand("Main Hand", ItemCategory.Weapon, ItemSlot.MainHand),
	OffHand("Off Hand", ItemCategory.Weapon, ItemSlot.OffHand),

	Relic("Relic", ItemCategory.Weapon, ItemSlot.Ranged),
	Ranged("Ranged", ItemCategory.Weapon, ItemSlot.Ranged),
	Thrown("Thrown", ItemCategory.Weapon, ItemSlot.Ranged),

	Quest("This Item Begins a Quest", ItemCategory.Quest),
	Bag("Bag", ItemCategory.Container),
	Projectile("Projectile", ItemCategory.Projectile),

	Token("<<<Token>>>", ItemCategory.Token),
	Enchant("<<<Enchant>>>", ItemCategory.Enchant),

	CraftingMaterial("<<<Crafting Material>>>", ItemCategory.CraftingMaterial),
	Pattern("<<<Pattern>>>", ItemCategory.Pattern),

	;

	private final String tooltipText;
	private final ItemCategory category;
	private final List<ItemSlot> itemSlots;

	ItemType(String tooltipText, ItemCategory category, ItemSlot... slots) {
		this.tooltipText = tooltipText;
		this.category = category;
		this.itemSlots = List.of(slots);
	}

	public static ItemType tryParse(String line) {
		return Stream.of(values()).filter(x -> x.tooltipText.equals(line)).findAny().orElse(null);
	}

	public static ItemType parse(String line) {
		if (line == null || line.isEmpty()) {
			return null;
		}
		ItemType result = tryParse(line);
		if (result == null) {
			throw new IllegalArgumentException(line);
		}
		return result;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public List<ItemSlot> getItemSlots() {
		return itemSlots;
	}

	public ItemSlot getUniqueItemSlot() {
		if (itemSlots.size() != 1) {
			throw new IllegalArgumentException("Required unique item slot for: " + this);
		}
		return itemSlots.get(0);
	}

	public boolean isEquippable() {
		return !itemSlots.isEmpty();
	}

	public boolean isEnchantable(ItemSubType subType) {
		switch (this) {
			case Head:
			case Shoulder:
			case Back:
			case Chest:
			case Wrist:
			case Hands:
			case Legs:
			case Feet:
			case Finger:
			case TwoHand:
			case OneHand:
			case MainHand:
				return true;
			case OffHand:
				return subType == WeaponSubType.Shield;
			case Ranged:
				return subType == WeaponSubType.Bow || subType == WeaponSubType.Crossbow || subType == WeaponSubType.Gun;
			default:
				return false;
		}
	}

	@Override
	public String toString() {
		return tooltipText;
	}
}

package wow.commons.model.categorization;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-01-01
 */
public enum ItemSlotGroup {
	Head(ItemSlot.Head),
	Neck(ItemSlot.Neck),
	Shoulder(ItemSlot.Shoulder),
	Back(ItemSlot.Back),
	Chest(ItemSlot.Chest),
	Shirt(ItemSlot.Shirt),
	Tabard(ItemSlot.Tabard),
	Wrist(ItemSlot.Wrist),
	Hands(ItemSlot.Hands),
	Waist(ItemSlot.Waist),
	Legs(ItemSlot.Legs),
	Feet(ItemSlot.Feet),
	Finger1(ItemSlot.Finger1),
	Finger2(ItemSlot.Finger2),
	Trinket1(ItemSlot.Trinket1),
	Trinket2(ItemSlot.Trinket2),
	MainHand(ItemSlot.MainHand),
	OffHand(ItemSlot.OffHand),
	Ranged(ItemSlot.Ranged),
	Fingers(ItemSlot.Finger1, ItemSlot.Finger2),
	Trinkets(ItemSlot.Trinket1, ItemSlot.Trinket2),
	Weapons(ItemSlot.MainHand, ItemSlot.OffHand),
	;

	private final List<ItemSlot> slots;

	ItemSlotGroup(ItemSlot slot) {
		this.slots = List.of(slot);
	}

	ItemSlotGroup(ItemSlot slot1, ItemSlot slot2) {
		this.slots = List.of(slot1, slot2);
	}

	public List<ItemSlot> getSlots() {
		return slots;
	}
}

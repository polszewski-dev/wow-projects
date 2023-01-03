import { ItemSlot } from "../equipment/ItemSlot";

export enum ItemSlotGroup {
	HEAD = 'HEAD',
	NECK = 'NECK',
	SHOULDER = 'SHOULDER',
	BACK = 'BACK',
	CHEST = 'CHEST',
	WRIST = 'WRIST',
	HANDS = 'HANDS',
	WAIST = 'WAIST',
	LEGS = 'LEGS',
	FEET = 'FEET',
	FINGERS = 'FINGERS',
	TRINKETS = 'TRINKETS',
	WEAPONS = 'WEAPONS',
	RANGED = 'RANGED',
}

export function getItemSlotGroup(itemSlot: ItemSlot): ItemSlotGroup | undefined {
	switch(itemSlot) {
		case ItemSlot.HEAD: return ItemSlotGroup.HEAD;
		case ItemSlot.NECK: return ItemSlotGroup.NECK;
		case ItemSlot.SHOULDER: return ItemSlotGroup.SHOULDER;
		case ItemSlot.BACK: return ItemSlotGroup.BACK;
		case ItemSlot.CHEST: return ItemSlotGroup.CHEST;
		case ItemSlot.WRIST: return ItemSlotGroup.WRIST;
		case ItemSlot.HANDS: return ItemSlotGroup.HANDS;
		case ItemSlot.WAIST: return ItemSlotGroup.WAIST;
		case ItemSlot.LEGS: return ItemSlotGroup.LEGS;
		case ItemSlot.FEET: return ItemSlotGroup.FEET;
		case ItemSlot.FINGER_1: return ItemSlotGroup.FINGERS;
		case ItemSlot.TRINKET_1: return ItemSlotGroup.TRINKETS;
		case ItemSlot.MAIN_HAND: return ItemSlotGroup.WEAPONS;
		case ItemSlot.RANGED: return ItemSlotGroup.RANGED;
		default:
			return undefined;
	}
}
package wow.commons.model.categorization;

import lombok.Getter;
import wow.commons.util.EnumUtil;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-01-01
 */
@Getter
public enum ItemSlotGroup {
	HEAD(ItemSlot.HEAD),
	NECK(ItemSlot.NECK),
	SHOULDER(ItemSlot.SHOULDER),
	BACK(ItemSlot.BACK),
	CHEST(ItemSlot.CHEST),
	SHIRT(ItemSlot.SHIRT),
	TABARD(ItemSlot.TABARD),
	WRIST(ItemSlot.WRIST),
	HANDS(ItemSlot.HANDS),
	WAIST(ItemSlot.WAIST),
	LEGS(ItemSlot.LEGS),
	FEET(ItemSlot.FEET),
	FINGER_1(ItemSlot.FINGER_1),
	FINGER_2(ItemSlot.FINGER_2),
	TRINKET_1(ItemSlot.TRINKET_1),
	TRINKET_2(ItemSlot.TRINKET_2),
	MAIN_HAND(ItemSlot.MAIN_HAND),
	OFF_HAND(ItemSlot.OFF_HAND),
	RANGED(ItemSlot.RANGED),
	FINGERS(ItemSlot.FINGER_1, ItemSlot.FINGER_2),
	TRINKETS(ItemSlot.TRINKET_1, ItemSlot.TRINKET_2),
	WEAPONS(ItemSlot.MAIN_HAND, ItemSlot.OFF_HAND);

	private final List<ItemSlot> slots;

	ItemSlotGroup(ItemSlot slot) {
		this.slots = List.of(slot);
	}

	ItemSlotGroup(ItemSlot slot1, ItemSlot slot2) {
		this.slots = List.of(slot1, slot2);
	}

	public static ItemSlotGroup parse(String value) {
		return EnumUtil.parse(value, values());
	}
}

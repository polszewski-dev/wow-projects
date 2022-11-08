package wow.commons.model.categorization;

import wow.commons.util.EnumUtil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public enum ItemSlot {
	HEAD,
	NECK,
	SHOULDER,
	BACK,
	CHEST,
	SHIRT,
	TABARD,
	WRIST,
	HANDS,
	WAIST,
	LEGS,
	FEET,
	FINGER_1,
	FINGER_2,
	TRINKET_1,
	TRINKET_2,
	MAIN_HAND,
	OFF_HAND,
	RANGED;

	public static ItemSlot parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public Set<ItemType> getItemTypes() {
		return Stream.of(ItemType.values())
					 .filter(itemType -> itemType.getItemSlots().contains(this))
					 .collect(Collectors.toSet());
	}
}

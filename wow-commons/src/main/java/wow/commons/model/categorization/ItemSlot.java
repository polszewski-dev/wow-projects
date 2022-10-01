package wow.commons.model.categorization;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public enum ItemSlot {
	Head,
	Neck,
	Shoulder,
	Back,
	Chest,
	Shirt,
	Tabard,
	Wrist,
	Hands,
	Waist,
	Legs,
	Feet,
	Finger1,
	Finger2,
	Trinket1,
	Trinket2,
	MainHand,
	OffHand,
	Ranged
	;

	public static ItemSlot parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return Stream.of(values()).filter(x -> x.name().equalsIgnoreCase(value)).findAny().orElseThrow(() -> new IllegalArgumentException(value));
	}

	public ItemType getUniqueItemType() {
		List<ItemType> list = Stream.of(ItemType.values())
									   .filter(itemType -> itemType.getItemSlots().contains(this))
									   .collect(Collectors.toList());
		if (list.size() == 1) {
			return list.get(0);
		}

		throw new IllegalArgumentException("Expected unique result for: " + this + " but found instead: " + list);
	}

	public Set<ItemType> getItemTypes() {
		return Stream.of(ItemType.values())
					 .filter(itemType -> itemType.getItemSlots().contains(this))
					 .collect(Collectors.toSet());
	}
}

package wow.commons.model.categorization;

import wow.commons.util.EnumUtil;

import java.util.List;
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
	SHIRT(false),
	TABARD(false),
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

	private final boolean dpsSlot;

	ItemSlot() {
		this(true);
	}

	ItemSlot(boolean dpsSlot) {
		this.dpsSlot = dpsSlot;
	}

	public static ItemSlot parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean isDpsSlot() {
		return dpsSlot;
	}

	public static List<ItemSlot> getDpsSlots() {
		return Stream.of(values())
				.filter(ItemSlot::isDpsSlot)
				.toList();
	}
}

package wow.scraper.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
public enum WowheadItemCategory {
	HEAD,
	SHOULDER,
	CHEST,
	WRIST,
	HANDS,
	WAIST,
	LEGS,
	FEET,

	AMULETS,
	RINGS,
	TRINKETS,
	CLOAKS,

	OFF_HANDS,

	DAGGERS,
	ONE_HANDED_SWORDS,
	STAVES,
	WANDS,

	GEMS(false),
	TOKENS(false),
	ITEM_STARTING_QUEST(false);

	private final boolean equipment;

	WowheadItemCategory(boolean equipment) {
		this.equipment = equipment;
	}

	WowheadItemCategory() {
		this(true);
	}

	public boolean isEquipment() {
		return equipment;
	}

	public static List<WowheadItemCategory> equipment() {
		return Stream.of(values())
				.filter(x -> x.equipment)
				.collect(Collectors.toList());
	}
}

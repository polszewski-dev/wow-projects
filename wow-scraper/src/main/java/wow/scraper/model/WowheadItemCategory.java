package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@AllArgsConstructor
@Getter
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
	QUEST(false);

	private final boolean equipment;

	WowheadItemCategory() {
		this(true);
	}

	public static List<WowheadItemCategory> equipment() {
		return Stream.of(values())
				.filter(x -> x.equipment)
				.toList();
	}
}

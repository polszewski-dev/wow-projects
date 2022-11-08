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

	GEMS,

	TOKENS;

	public static List<WowheadItemCategory> equipment() {
		return Stream.of(values()).filter(x -> x != GEMS && x != TOKENS).collect(Collectors.toList());
	}
}

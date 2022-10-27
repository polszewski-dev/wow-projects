package wow.scraper.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
public enum WowheadItemCategory {
	head,
	shoulder,
	chest,
	wrist,
	hands,
	waist,
	legs,
	feet,

	amulets,
	rings,
	trinkets,
	cloaks,

	off_hands,

	daggers,
	one_handed_swords,
	staves,
	wands,

	gems,

	;

	public static List<WowheadItemCategory> equipment() {
		return Stream.of(values()).filter(x -> x != gems).collect(Collectors.toList());
	}
}

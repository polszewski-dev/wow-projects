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
	HEAD("items/armor/cloth/slot:1"),
	SHOULDER("items/armor/cloth/slot:3"),
	CHEST("items/armor/cloth/slot:5"),
	WRIST("items/armor/cloth/slot:9"),
	HANDS("items/armor/cloth/slot:10"),
	WAIST("items/armor/cloth/slot:6"),
	LEGS("items/armor/cloth/slot:7"),
	FEET("items/armor/cloth/slot:8"),

	AMULETS("items/armor/amulets"),
	RINGS("items/armor/rings"),
	TRINKETS("items/armor/trinkets"),
	CLOAKS("items/armor/cloaks"),

	OFF_HANDS("items/armor/off-hand-frills"),

	DAGGERS("items/weapons/daggers"),
	ONE_HANDED_SWORDS("items/weapons/one-handed-swords"),
	ONE_HANDED_MACES("items/weapons/one-handed-maces"),
	STAVES("items/weapons/staves"),
	WANDS("items/weapons/wands"),

	GEMS("items/gems/type:0:1:2:3:4:5:6:8?filter=81;7;0", false),
	TOKENS("items/miscellaneous/armor-tokens", false),
	QUEST("items/quest/quality:3:4:5?filter=6;1;0", false),

	ENCHANTS_PERMANENT("items/consumables/item-enhancements-permanent", false);

	private final String url;
	private final boolean equipment;

	WowheadItemCategory(String url) {
		this(url, true);
	}

	public static List<WowheadItemCategory> equipment() {
		return Stream.of(values())
				.filter(x -> x.equipment)
				.toList();
	}
}

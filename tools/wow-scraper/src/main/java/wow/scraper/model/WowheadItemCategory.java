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
	CLOTH_HEAD("items/armor/cloth/slot:1"),
	CLOTH_SHOULDER("items/armor/cloth/slot:3"),
	CLOTH_CHEST("items/armor/cloth/slot:5"),
	CLOTH_WRIST("items/armor/cloth/slot:9"),
	CLOTH_HANDS("items/armor/cloth/slot:10"),
	CLOTH_WAIST("items/armor/cloth/slot:6"),
	CLOTH_LEGS("items/armor/cloth/slot:7"),
	CLOTH_FEET("items/armor/cloth/slot:8"),

	LEATHER_HEAD("items/armor/leather/slot:1"),
	LEATHER_SHOULDER("items/armor/leather/slot:3"),
	LEATHER_CHEST("items/armor/leather/slot:5"),
	LEATHER_WRIST("items/armor/leather/slot:9"),
	LEATHER_HANDS("items/armor/leather/slot:10"),
	LEATHER_WAIST("items/armor/leather/slot:6"),
	LEATHER_LEGS("items/armor/leather/slot:7"),
	LEATHER_FEET("items/armor/leather/slot:8"),

	MAIL_HEAD("items/armor/mail/slot:1"),
	MAIL_SHOULDER("items/armor/mail/slot:3"),
	MAIL_CHEST("items/armor/mail/slot:5"),
	MAIL_WRIST("items/armor/mail/slot:9"),
	MAIL_HANDS("items/armor/mail/slot:10"),
	MAIL_WAIST("items/armor/mail/slot:6"),
	MAIL_LEGS("items/armor/mail/slot:7"),
	MAIL_FEET("items/armor/mail/slot:8"),

	PLATE_HEAD("items/armor/plate/slot:1"),
	PLATE_SHOULDER("items/armor/plate/slot:3"),
	PLATE_CHEST("items/armor/plate/slot:5"),
	PLATE_WRIST("items/armor/plate/slot:9"),
	PLATE_HANDS("items/armor/plate/slot:10"),
	PLATE_WAIST("items/armor/plate/slot:6"),
	PLATE_LEGS("items/armor/plate/slot:7"),
	PLATE_FEET("items/armor/plate/slot:8"),

	AMULETS("items/armor/amulets"),
	RINGS("items/armor/rings"),
	TRINKETS("items/armor/trinkets"),
	CLOAKS("items/armor/cloaks"),

	OFF_HANDS("items/armor/off-hand-frills"),
	SHIELDS("items/armor/shields"),

	IDOLS("items/armor/idols"),
	TOTEMS("items/armor/totems"),
	LIBRAMS("items/armor/librams"),

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

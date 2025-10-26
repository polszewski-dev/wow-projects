package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

import static wow.scraper.model.WowheadItemCategory.EquipmentGroup.*;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@AllArgsConstructor
@Getter
public enum WowheadItemCategory {
	CLOTH_HEAD("items/armor/cloth/slot:1", CLOTH_ARMOR),
	CLOTH_SHOULDER("items/armor/cloth/slot:3", CLOTH_ARMOR),
	CLOTH_CHEST("items/armor/cloth/slot:5", CLOTH_ARMOR),
	CLOTH_WRIST("items/armor/cloth/slot:9", CLOTH_ARMOR),
	CLOTH_HANDS("items/armor/cloth/slot:10", CLOTH_ARMOR),
	CLOTH_WAIST("items/armor/cloth/slot:6", CLOTH_ARMOR),
	CLOTH_LEGS("items/armor/cloth/slot:7", CLOTH_ARMOR),
	CLOTH_FEET("items/armor/cloth/slot:8", CLOTH_ARMOR),

	LEATHER_HEAD("items/armor/leather/slot:1", LEATHER_ARMOR),
	LEATHER_SHOULDER("items/armor/leather/slot:3", LEATHER_ARMOR),
	LEATHER_CHEST("items/armor/leather/slot:5", LEATHER_ARMOR),
	LEATHER_WRIST("items/armor/leather/slot:9", LEATHER_ARMOR),
	LEATHER_HANDS("items/armor/leather/slot:10", LEATHER_ARMOR),
	LEATHER_WAIST("items/armor/leather/slot:6", LEATHER_ARMOR),
	LEATHER_LEGS("items/armor/leather/slot:7", LEATHER_ARMOR),
	LEATHER_FEET("items/armor/leather/slot:8", LEATHER_ARMOR),

	MAIL_HEAD("items/armor/mail/slot:1", MAIL_ARMOR),
	MAIL_SHOULDER("items/armor/mail/slot:3", MAIL_ARMOR),
	MAIL_CHEST("items/armor/mail/slot:5", MAIL_ARMOR),
	MAIL_WRIST("items/armor/mail/slot:9", MAIL_ARMOR),
	MAIL_HANDS("items/armor/mail/slot:10", MAIL_ARMOR),
	MAIL_WAIST("items/armor/mail/slot:6", MAIL_ARMOR),
	MAIL_LEGS("items/armor/mail/slot:7", MAIL_ARMOR),
	MAIL_FEET("items/armor/mail/slot:8", MAIL_ARMOR),

	PLATE_HEAD("items/armor/plate/slot:1", PLATE_ARMOR),
	PLATE_SHOULDER("items/armor/plate/slot:3", PLATE_ARMOR),
	PLATE_CHEST("items/armor/plate/slot:5", PLATE_ARMOR),
	PLATE_WRIST("items/armor/plate/slot:9", PLATE_ARMOR),
	PLATE_HANDS("items/armor/plate/slot:10", PLATE_ARMOR),
	PLATE_WAIST("items/armor/plate/slot:6", PLATE_ARMOR),
	PLATE_LEGS("items/armor/plate/slot:7", PLATE_ARMOR),
	PLATE_FEET("items/armor/plate/slot:8", PLATE_ARMOR),

	AMULETS("items/armor/amulets", MISC),
	RINGS("items/armor/rings", MISC),
	TRINKETS("items/armor/trinkets", MISC),
	CLOAKS("items/armor/cloaks", MISC),

	OFF_HANDS("items/armor/off-hand-frills", MISC),
	SHIELDS("items/armor/shields", MISC),

	IDOLS("items/armor/idols", MISC),
	TOTEMS("items/armor/totems", MISC),
	LIBRAMS("items/armor/librams", MISC),

	DAGGERS("items/weapons/daggers", WEAPONS),
	ONE_HANDED_SWORDS("items/weapons/one-handed-swords", WEAPONS),
	ONE_HANDED_MACES("items/weapons/one-handed-maces", WEAPONS),
	STAVES("items/weapons/staves", WEAPONS),
	WANDS("items/weapons/wands", WEAPONS),

	GEMS("items/gems/type:0:1:2:3:4:5:6:8?filter=81;7;0"),
	TOKENS("items/miscellaneous/armor-tokens"),
	QUEST("items/quest/quality:3:4:5?filter=6;1;0"),

	ENCHANTS_PERMANENT("items/consumables/item-enhancements-permanent");

	public enum EquipmentGroup {
		CLOTH_ARMOR,
		LEATHER_ARMOR,
		MAIL_ARMOR,
		PLATE_ARMOR,
		WEAPONS,
		MISC
	}

	private final String url;
	private final EquipmentGroup equipmentGroup;

	WowheadItemCategory(String url) {
		this(url, null);
	}

	public boolean isEquipment() {
		return equipmentGroup != null;
	}

	public static List<WowheadItemCategory> equipment() {
		return Stream.of(values())
				.filter(x -> x.equipmentGroup != null)
				.toList();
	}

	public static List<WowheadItemCategory> withGroup(EquipmentGroup equipmentGroup) {
		return Stream.of(values())
				.filter(x -> x.equipmentGroup == equipmentGroup)
				.toList();
	}
}

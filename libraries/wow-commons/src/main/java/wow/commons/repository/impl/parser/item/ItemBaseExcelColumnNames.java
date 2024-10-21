package wow.commons.repository.impl.parser.item;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public final class ItemBaseExcelColumnNames {
	public static final String ITEM_TYPE = "item_type";
	public static final String ITEM_SUBTYPE = "item_subtype";

	public static final String RARITY = "rarity";
	public static final String BINDING = "binding";
	public static final String UNIQUE = "unique";
	public static final String ITEM_LEVEL = "item_level";
	public static final String SOURCE = "source";

	public static final String ITEM_SOCKET_TYPES = "socket_types";
	public static final String ITEM_ITEM_SET = "item_set";
	public static final String ITEM_EFFECT_PREFIX = "";
	public static final String ITEM_ACTIVATED_ABILITY = "activated_ability";
	public static final int ITEM_MAX_EFFECTS = 10;

	public static final String ENCHANT_ITEM_TYPES = "item_types";
	public static final String ENCHANT_ITEM_SUBTYPES = "item_subtypes";
	public static final String ENCHANT_REQ_ILVL = "req_ilvl";
	public static final String ENCHANT_ENCHANT_ID = "enchant_id";
	public static final String ENCHANT_EFFECT_PREFIX = "";

	public static final String SOCKET_BONUS_PREFIX = "sb_";

	public static final String GEM_COLOR = "color";
	public static final String GEM_META_ENABLERS = "meta_enablers";
	public static final String GEM_EFFECT_PREFIX = "";
	public static final int GEM_MAX_EFFECTS = 2;

	public static final String ITEM_SET_BONUS_REQ_PROFESSION = "bonus_req_prof";
	public static final String ITEM_SET_BONUS_REQ_PROFESSION_LEVEL = "bonus_req_prof_lvl";
	public static final int ITEM_SET_MAX_BONUSES = 6;

	public static String itemSetBonusNumPieces(int bonusIdx) {
		return itemSetBonus(bonusIdx) + "_pieces";
	}

	public static String itemSetBonusStatPrefix(int bonusIdx) {
		return itemSetBonus(bonusIdx) + "_";
	}

	private static String itemSetBonus(int bonusIdx) {
		return "bonus" + bonusIdx;
	}

	private ItemBaseExcelColumnNames() {}
}

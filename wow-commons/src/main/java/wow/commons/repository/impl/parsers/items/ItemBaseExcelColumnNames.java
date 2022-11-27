package wow.commons.repository.impl.parsers.items;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public final class ItemBaseExcelColumnNames {
	public static final String ID = "id";

	public static final String ITEM_TYPE = "item_type";
	public static final String ITEM_SUBTYPE = "item_subtype";

	public static final String RARITY = "rarity";
	public static final String BINDING = "binding";
	public static final String UNIQUE = "unique";
	public static final String ITEM_LEVEL = "item_level";
	public static final String SOURCE = "source";

	public static final String ITEM_SOCKET_TYPES = "socket_types";
	public static final String ITEM_ITEM_SET = "item_set";
	public static final int ITEM_MAX_STATS = 10;

	public static final String SOCKET_BONUS_PREFIX = "sb_";
	public static final int SOCKET_BONUS_MAX_STATS = 2;

	public static final String GEM_COLOR = "color";
	public static final String GEM_META_ENABLERS = "meta_enablers";
	public static final int GEM_MAX_STATS = 3;

	public static final String ITEM_SET_NAME = "name";
	public static final int ITEM_SET_MAX_BONUSES = 6;
	public static final int ITEM_SET_BONUS_MAX_STATS = 2;

	public static String itemSetBonusNumPieces(int bonusIdx) {
		return itemSetBonus(bonusIdx) + "_pieces";
	}

	public static String itemSetBonusDescription(int bonusIdx) {
		return itemSetBonus(bonusIdx) + "_descr";
	}

	public static String itemSetBonusStatPrefix(int bonusIdx) {
		return itemSetBonus(bonusIdx) + "_";
	}

	private static String itemSetBonus(int bonusIdx) {
		return "bonus" + bonusIdx;
	}

	private ItemBaseExcelColumnNames() {}
}

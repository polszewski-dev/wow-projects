package wow.commons.model.item;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-24
 */
@AllArgsConstructor
public enum MetaEnabler {
	AT_LEAST_1_RED("Requires at least 1 Red Gem", 1, 0, 0),
	AT_LEAST_2_REDS("Requires at least 2 Red Gems", 2, 0, 0),
	AT_LEAST_3_REDS("Requires at least 3 Red Gems", 3, 0, 0),

	AT_LEAST_1_YELLOW("Requires at least 1 Yellow Gem", 0, 1, 0),
	AT_LEAST_2_YELLOWS("Requires at least 2 Yellow Gems", 0, 2, 0),
	AT_LEAST_3_YELLOWS("Requires at least 3 Yellow Gems", 0, 3, 0),

	AT_LEAST_2_BLUES("Requires at least 2 Blue Gems", 0, 0, 2),
	AT_LEAST_3_BLUES("Requires at least 3 Blue Gems", 0, 0, 3),
	AT_LEAST_5_BLUES("Requires at least 5 Blue Gems", 0, 0, 5),

	MORE_RED_THAN_YELLOW("Requires more Red gems than Yellow gems", true, false, false),
	MORE_RED_THAN_BLUE("Requires more Red gems than Blue gems", false, true, false),
	MORE_BLUE_THAN_YELLOW("Requires more Blue gems than Yellow gems", false, false, true);

	private final String string;
	private final int reqRed;
	private final int reqYellow;
	private final int reqBlue;
	private final boolean moreRedThanYellow;
	private final boolean moreRedThanBlue;
	private final boolean moreBlueThanYellow;

	MetaEnabler(String string, int reqRed, int reqYellow, int reqBlue) {
		this(string, reqRed, reqYellow, reqBlue, false, false, false);
	}

	MetaEnabler(String string, boolean moreRedThanYellow, boolean moreRedThanBlue, boolean moreBlueThanYellow) {
		this(string, 0, 0, 0, moreRedThanYellow, moreRedThanBlue, moreBlueThanYellow);
	}

	public static MetaEnabler tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.string);
	}

	public boolean isMetaConditionTrue(int numRed, int numYellow, int numBlue) {
		if (numRed < reqRed) {
			return false;
		}
		if (numYellow < reqYellow) {
			return false;
		}
		if (numBlue < reqBlue) {
			return false;
		}
		if (moreRedThanYellow && numRed <= numYellow) {
			return false;
		}
		if (moreRedThanBlue && numRed <= numBlue) {
			return false;
		}
		return !moreBlueThanYellow || numBlue > numYellow;
	}

	@Override
	public String toString() {
		return string;
	}
}

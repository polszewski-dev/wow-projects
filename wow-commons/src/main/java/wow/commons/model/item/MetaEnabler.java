package wow.commons.model.item;

/**
 * User: POlszewski
 * Date: 2021-03-24
 */
public enum MetaEnabler {
	AtLeast1Red("Requires at least 1 Red gem", 1, 0, 0),
	AtLeast2Reds("Requires at least 2 Red gems", 2, 0, 0),
	AtLeast3Reds("Requires at least 3 Red gems", 3, 0, 0),

	AtLeast1Yellow("Requires at least 1 Yellow gem", 0, 1, 0),
	AtLeast2Yellows("Requires at least 2 Yellow gems", 0, 2, 0),

	AtLeast2Blues("Requires at least 2 Blue gems", 0, 0, 2),
	AtLeast3Blues("Requires at least 3 Blue gems", 0, 0, 3),
	AtLeast5Blues("Requires at least 5 Blue gems", 0, 0, 5),

	MoreRedThanYellow("Requires more Red gems than Yellow gems", true, false, false),
	MoreRedThanBlue("Requires more Red gems than Blue gems", false, true, false),
	MoreBlueThanYellow("Requires more Blue gems than Yellow gems", false, false, true),
	;

	private final String string;
	private final int reqRed;
	private final int reqYellow;
	private final int reqBlue;
	private final boolean moreRedThanYellow;
	private final boolean moreRedThanBlue;
	private final boolean moreBlueThanYellow;

	MetaEnabler(String string, int reqRed, int reqYellow, int reqBlue, boolean moreRedThanYellow, boolean moreRedThanBlue, boolean moreBlueThanYellow) {
		this.string = string;
		this.reqRed = reqRed;
		this.reqYellow = reqYellow;
		this.reqBlue = reqBlue;
		this.moreRedThanYellow = moreRedThanYellow;
		this.moreRedThanBlue = moreRedThanBlue;
		this.moreBlueThanYellow = moreBlueThanYellow;
	}

	MetaEnabler(String string, int reqRed, int reqYellow, int reqBlue) {
		this(string, reqRed, reqYellow, reqBlue, false, false, false);
	}

	MetaEnabler(String string, boolean moreRedThanYellow, boolean moreRedThanBlue, boolean moreBlueThanYellow) {
		this(string, 0, 0, 0, moreRedThanYellow, moreRedThanBlue, moreBlueThanYellow);
	}

	public static MetaEnabler tryParse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		for (MetaEnabler metaEnabler : values()) {
			if (metaEnabler.string.equals(value)) {
				return metaEnabler;
			}
		}
		return null;
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
		if (moreRedThanYellow && !(numRed > numYellow)) {
			return false;
		}
		if (moreRedThanBlue && !(numRed > numBlue)) {
			return false;
		}
		if (moreBlueThanYellow && !(numBlue > numYellow)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return string;
	}
}

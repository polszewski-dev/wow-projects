package wow.scraper.model;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-27
 */
public enum WowheadSource {
	CRAFTED(1),
	DROP(2),
	PVP_ARENA(3),
	QUEST(4),
	BADGES(5);

	private final int code;

	WowheadSource(int code) {
		this.code = code;
	}

	public static WowheadSource fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}

	public int getCode() {
		return code;
	}
}

package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-27
 */
@AllArgsConstructor
@Getter
public enum WowheadSource {
	CRAFTED(1),
	DROP(2),
	PVP_ARENA(3),
	QUEST(4),
	BADGES(5);

	private final int code;

	public static WowheadSource fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}
}

package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.pve.Side;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-06-21
 */
@AllArgsConstructor
@Getter
public enum WowheadSide {
	NONE(0, null),
	ALLIANCE(1, Side.ALLIANCE),
	HORDE(2, Side.HORDE);

	private final int code;
	private final Side side;

	public static WowheadSide fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}
}

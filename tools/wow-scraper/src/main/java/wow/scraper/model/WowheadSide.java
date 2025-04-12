package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.pve.Side;

import java.util.List;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-06-21
 */
@AllArgsConstructor
@Getter
public enum WowheadSide {
	NONE(0, List.of()),
	ALLIANCE(1, List.of(Side.ALLIANCE)),
	HORDE(2, List.of(Side.HORDE)),
	BOTH(3, List.of(Side.HORDE, Side.ALLIANCE));

	private final int code;
	private final List<Side> sides;

	public static WowheadSide fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}
}

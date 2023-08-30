package wow.commons.model.spells;

import wow.commons.model.Duration;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record CastInfo(
		Cost cost,
		Duration castTime,
		boolean channeled
) {
	public CastInfo {
		Objects.requireNonNull(castTime);
		Objects.requireNonNull(cost);
	}
}

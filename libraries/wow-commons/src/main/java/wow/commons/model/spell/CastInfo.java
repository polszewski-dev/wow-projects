package wow.commons.model.spell;

import wow.commons.model.Duration;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record CastInfo(
		Duration castTime,
		boolean channeled,
		boolean ignoresGcd
) {
	public CastInfo {
		Objects.requireNonNull(castTime);
	}
}

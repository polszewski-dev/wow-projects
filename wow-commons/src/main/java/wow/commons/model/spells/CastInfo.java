package wow.commons.model.spells;

import wow.commons.model.Duration;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record CastInfo(
		int manaCost,
		Duration castTime,
		boolean channeled,
		AdditionalCost additionalCost,
		AppliedEffect appliedEffect
) {
	public CastInfo {
		Objects.requireNonNull(castTime);
	}
}

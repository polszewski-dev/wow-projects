package wow.minmax.model;

import wow.commons.model.buff.Buff;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
public record BuffStatus(
		Buff buff,
		boolean enabled
) {
	public BuffStatus {
		Objects.requireNonNull(buff);
	}
}

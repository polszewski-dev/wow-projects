package wow.minmax.model;

import wow.commons.model.effect.Effect;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public record SocketBonusStatus(
		Effect bonus,
		boolean enabled
) {
}

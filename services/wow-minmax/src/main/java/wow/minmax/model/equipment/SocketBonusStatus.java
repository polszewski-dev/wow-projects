package wow.minmax.model.equipment;

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

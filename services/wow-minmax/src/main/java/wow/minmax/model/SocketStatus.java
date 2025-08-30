package wow.minmax.model;

import wow.commons.model.item.SocketType;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public record SocketStatus(
		int socketNo,
		SocketType socketType,
		boolean matching
) {
}

package wow.minmax.model.equipment;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public record ItemSocketStatus(
		List<SocketStatus> socketStatuses,
		SocketBonusStatus socketBonusStatus
) {
}

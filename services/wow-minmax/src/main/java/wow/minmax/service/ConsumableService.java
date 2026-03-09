package wow.minmax.service;

import wow.minmax.model.ConsumableStatus;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public interface ConsumableService {
	List<ConsumableStatus> getConsumableStatuses(PlayerId playerId);

	List<ConsumableStatus> getConsumableStatuses(Player player);

	Player changeConsumableStatus(PlayerId playerId, String consumableName, boolean enabled);
}

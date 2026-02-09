package wow.minmax.service;

import wow.minmax.model.CharacterId;
import wow.minmax.model.ConsumableStatus;
import wow.minmax.model.Player;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public interface ConsumableService {
	List<ConsumableStatus> getConsumableStatuses(CharacterId characterId);

	List<ConsumableStatus> getConsumableStatuses(Player player);

	Player changeConsumableStatus(CharacterId characterId, String consumableName, boolean enabled);
}

package wow.minmax.service;

import wow.character.model.character.PlayerCharacter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.ConsumableStatus;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public interface ConsumableService {
	List<ConsumableStatus> getConsumableStatuses(CharacterId characterId);

	List<ConsumableStatus> getConsumableStatuses(PlayerCharacter player);

	PlayerCharacter changeConsumableStatus(CharacterId characterId, String consumableName, boolean enabled);
}

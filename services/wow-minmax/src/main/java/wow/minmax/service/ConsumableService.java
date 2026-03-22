package wow.minmax.service;

import wow.character.model.character.OptionStatus;
import wow.commons.model.item.Consumable;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public interface ConsumableService {
	List<OptionStatus<Consumable>> getConsumableStatuses(PlayerId playerId);

	List<OptionStatus<Consumable>> getConsumableStatuses(Player player);

	Player changeConsumableStatus(PlayerId playerId, String consumableName, boolean enabled);
}

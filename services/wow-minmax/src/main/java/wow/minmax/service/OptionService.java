package wow.minmax.service;

import wow.character.model.character.OptionStatus;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.item.Consumable;
import wow.commons.model.item.ConsumableId;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-21
 */
public interface OptionService {
	List<OptionStatus<Buff>> getBuffStatuses(PlayerId playerId);

	Player changeBuffStatus(PlayerId playerId, BuffId buffId, boolean enabled);

	List<OptionStatus<Consumable>> getConsumableStatuses(PlayerId playerId);

	Player changeConsumableStatus(PlayerId playerId, ConsumableId consumableId, boolean enabled);
}

package wow.minmax.service;

import wow.character.model.character.BuffListType;
import wow.character.model.character.OptionStatus;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
public interface BuffService {
	List<OptionStatus<Buff>> getBuffStatuses(PlayerId playerId, BuffListType buffListType);

	Player changeBuffStatus(PlayerId playerId, BuffListType buffListType, BuffId buffId, boolean enabled);
}

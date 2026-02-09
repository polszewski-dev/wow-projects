package wow.minmax.service;

import wow.character.model.character.BuffListType;
import wow.commons.model.buff.BuffId;
import wow.minmax.model.BuffStatus;
import wow.minmax.model.CharacterId;
import wow.minmax.model.Player;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
public interface BuffService {
	List<BuffStatus> getBuffStatuses(CharacterId characterId, BuffListType buffListType);

	List<BuffStatus> getBuffStatuses(Player player, BuffListType buffListType);

	Player changeBuffStatus(CharacterId characterId, BuffListType buffListType, BuffId buffId, boolean enabled);
}

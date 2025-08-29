package wow.minmax.service;

import wow.character.model.character.BuffListType;
import wow.character.model.character.PlayerCharacter;
import wow.commons.model.buff.BuffId;
import wow.minmax.model.BuffStatus;
import wow.minmax.model.CharacterId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
public interface BuffService {
	List<BuffStatus> getBuffStatuses(CharacterId characterId, BuffListType buffListType);

	List<BuffStatus> getBuffStatuses(PlayerCharacter player, BuffListType buffListType);

	PlayerCharacter changeBuffStatus(CharacterId characterId, BuffListType buffListType, BuffId buffId, int rank, boolean enabled);
}

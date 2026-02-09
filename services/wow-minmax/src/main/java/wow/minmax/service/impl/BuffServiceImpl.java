package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.BuffListType;
import wow.commons.model.buff.BuffId;
import wow.minmax.model.BuffStatus;
import wow.minmax.model.CharacterId;
import wow.minmax.model.Player;
import wow.minmax.service.BuffService;
import wow.minmax.service.PlayerCharacterService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
@Service
@AllArgsConstructor
public class BuffServiceImpl implements BuffService {
	private final PlayerCharacterService playerCharacterService;

	@Override
	public List<BuffStatus> getBuffStatuses(CharacterId characterId, BuffListType buffListType) {
		var player = playerCharacterService.getPlayer(characterId);

		return getBuffStatuses(player, buffListType);
	}

	@Override
	public List<BuffStatus> getBuffStatuses(Player player, BuffListType buffListType) {
		var buffs = player.getBuffList(buffListType);

		return buffs.getAvailableHighestRanks().stream()
				.map(buff -> new BuffStatus(buff, buffs.has(buff.getName())))
				.toList();
	}

	@Override
	public Player changeBuffStatus(CharacterId characterId, BuffListType buffListType, BuffId buffId, boolean enabled) {
		var player = playerCharacterService.getPlayer(characterId);

		player.getBuffList(buffListType).enable(buffId, enabled);

		playerCharacterService.saveCharacter(characterId, player);

		return player;
	}
}

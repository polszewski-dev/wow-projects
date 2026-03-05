package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.BuffListType;
import wow.commons.model.buff.BuffId;
import wow.minmax.model.BuffStatus;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;
import wow.minmax.service.BuffService;
import wow.minmax.service.PlayerService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
@Service
@AllArgsConstructor
public class BuffServiceImpl implements BuffService {
	private final PlayerService playerService;

	@Override
	public List<BuffStatus> getBuffStatuses(PlayerId playerId, BuffListType buffListType) {
		var player = playerService.getPlayer(playerId);

		return getBuffStatuses(player, buffListType);
	}

	@Override
	public List<BuffStatus> getBuffStatuses(Player player, BuffListType buffListType) {
		var buffs = player.getBuffList(buffListType);

		return buffs.getAvailable().stream()
				.map(buff -> new BuffStatus(buff, buffs.has(buff.getName())))
				.toList();
	}

	@Override
	public Player changeBuffStatus(PlayerId playerId, BuffListType buffListType, BuffId buffId, boolean enabled) {
		var player = playerService.getPlayer(playerId);

		player.getBuffList(buffListType).enable(buffId, enabled);

		playerService.savePlayer(playerId, player);

		return player;
	}
}

package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.OptionStatus;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
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
	public List<OptionStatus<Buff>> getBuffStatuses(PlayerId playerId) {
		var player = playerService.getPlayer(playerId);

		return player.getBuffs().getStatuses();
	}

	@Override
	public Player changeBuffStatus(PlayerId playerId, BuffId buffId, boolean enabled) {
		var player = playerService.getPlayer(playerId);

		player.getBuffs().enable(buffId, enabled);

		playerService.savePlayer(player);

		return player;
	}
}

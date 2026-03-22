package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.OptionStatus;
import wow.commons.model.item.Consumable;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;
import wow.minmax.service.ConsumableService;
import wow.minmax.service.PlayerService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Service
@AllArgsConstructor
public class ConsumableServiceImpl implements ConsumableService {
	private final PlayerService playerService;

	@Override
	public List<OptionStatus<Consumable>> getConsumableStatuses(PlayerId playerId) {
		var player = playerService.getPlayer(playerId);

		return getConsumableStatuses(player);
	}

	@Override
	public List<OptionStatus<Consumable>> getConsumableStatuses(Player player) {
		return player.getConsumables().getStatuses();
	}

	@Override
	public Player changeConsumableStatus(PlayerId playerId, String consumableName, boolean enabled) {
		var player = playerService.getPlayer(playerId);

		player.getConsumables().enable(consumableName, enabled);

		playerService.savePlayer(player);

		return player;
	}
}

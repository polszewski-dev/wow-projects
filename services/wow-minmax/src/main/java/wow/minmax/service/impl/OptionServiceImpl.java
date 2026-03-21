package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.OptionStatus;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.item.Consumable;
import wow.commons.model.item.ConsumableId;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;
import wow.minmax.service.OptionService;
import wow.minmax.service.PlayerService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-21
 */
@Service
@AllArgsConstructor
public class OptionServiceImpl implements OptionService {
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

	@Override
	public List<OptionStatus<Consumable>> getConsumableStatuses(PlayerId playerId) {
		var player = playerService.getPlayer(playerId);

		return player.getConsumables().getStatuses();
	}

	@Override
	public Player changeConsumableStatus(PlayerId playerId, ConsumableId consumableId, boolean enabled) {
		var player = playerService.getPlayer(playerId);

		player.getConsumables().enable(consumableId, enabled);

		playerService.savePlayer(player);

		return player;
	}
}

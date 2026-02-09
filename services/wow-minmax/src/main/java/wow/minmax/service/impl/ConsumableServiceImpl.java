package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.Consumables;
import wow.commons.model.item.Consumable;
import wow.minmax.model.CharacterId;
import wow.minmax.model.ConsumableStatus;
import wow.minmax.model.Player;
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
	public List<ConsumableStatus> getConsumableStatuses(CharacterId characterId) {
		var player = playerService.getPlayer(characterId);

		return getConsumableStatuses(player);
	}

	@Override
	public List<ConsumableStatus> getConsumableStatuses(Player player) {
		var consumables = player.getConsumables();
		var availableConsumables = consumables.getAvailable();

		return availableConsumables.stream()
				.map(consumable -> getConsumableStatus(consumable, consumables))
				.toList();
	}

	private ConsumableStatus getConsumableStatus(Consumable consumable, Consumables consumables) {
		return new ConsumableStatus(
				consumable,
				consumables.has(consumable.getId())
		);
	}

	@Override
	public Player changeConsumableStatus(CharacterId characterId, String consumableName, boolean enabled) {
		var player = playerService.getPlayer(characterId);

		player.getConsumables().enable(consumableName, enabled);

		playerService.saveCharacter(characterId, player);

		return player;
	}
}

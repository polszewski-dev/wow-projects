package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.character.model.character.PlayerCharacter;
import wow.commons.client.converter.ConsumableConverter;
import wow.commons.client.dto.ConsumableDTO;
import wow.commons.model.item.Consumable;
import wow.minmax.model.CharacterId;
import wow.minmax.service.PlayerCharacterService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-31
 */
@RestController
@RequestMapping("api/v1/consumables")
@AllArgsConstructor
@Slf4j
public class ConsumableController {
	private final PlayerCharacterService playerCharacterService;
	private final ConsumableConverter consumableConverter;

	@GetMapping("{characterId}")
	public List<ConsumableDTO> getConsumables(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);

		return getConsumableDTOs(player);
	}

	@PutMapping("{characterId}")
	public List<ConsumableDTO> enableConsumable(
			@PathVariable("characterId") CharacterId characterId,
			@RequestBody ConsumableDTO consumable
	) {
		var consumableName = consumable.name();
		var enabled = consumable.enabled();
		var player = playerCharacterService.enableConsumable(characterId, consumableName, enabled);

		log.info("Changed consumable charId: {}, name: {}, enabled: {}", characterId, consumableName, enabled);
		return getConsumableDTOs(player);
	}

	private List<ConsumableDTO> getConsumableDTOs(PlayerCharacter player) {
		var consumables = player.getConsumables();
		var availableConsumables = consumables.getAvailable();

		return availableConsumables.stream()
				.map(consumable -> getConsumableDTO(consumable, consumables.has(consumable.getName())))
				.toList();
	}

	private ConsumableDTO getConsumableDTO(Consumable consumable, boolean enabled) {
		return consumableConverter
				.convert(consumable)
				.withEnabled(enabled);
	}
}

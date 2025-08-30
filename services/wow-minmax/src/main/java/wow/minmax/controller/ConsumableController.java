package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.minmax.client.dto.ConsumableStatusDTO;
import wow.minmax.converter.dto.ConsumableStatusConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.service.ConsumableService;

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
	private final ConsumableService consumableService;
	private final ConsumableStatusConverter consumableStatusConverter;

	@GetMapping("{characterId}")
	public List<ConsumableStatusDTO> getConsumables(
			@PathVariable("characterId") CharacterId characterId
	) {
		var consumableStatuses = consumableService.getConsumableStatuses(characterId);

		return consumableStatusConverter.convertList(consumableStatuses);
	}

	@PutMapping("{characterId}")
	public List<ConsumableStatusDTO> enableConsumable(
			@PathVariable("characterId") CharacterId characterId,
			@RequestBody ConsumableStatusDTO consumableStatus
	) {
		var consumable = consumableStatus.consumable();
		var consumableName = consumable.name();
		var enabled = consumableStatus.enabled();

		var player = consumableService.changeConsumableStatus(characterId, consumableName, enabled);

		log.info("Changed consumable charId: {}, name: {}, enabled: {}", characterId, consumableName, enabled);

		var consumableStatuses = consumableService.getConsumableStatuses(player);

		return consumableStatusConverter.convertList(consumableStatuses);
	}
}

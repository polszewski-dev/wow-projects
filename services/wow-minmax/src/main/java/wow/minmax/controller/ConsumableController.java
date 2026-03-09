package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.minmax.client.dto.ConsumableStatusDTO;
import wow.minmax.converter.dto.ConsumableStatusConverter;
import wow.minmax.model.PlayerId;
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

	@GetMapping("{playerId}")
	public List<ConsumableStatusDTO> getConsumables(
			@PathVariable("playerId") PlayerId playerId
	) {
		var consumableStatuses = consumableService.getConsumableStatuses(playerId);

		return consumableStatusConverter.convertList(consumableStatuses);
	}

	@PutMapping("{playerId}")
	public List<ConsumableStatusDTO> enableConsumable(
			@PathVariable("playerId") PlayerId playerId,
			@RequestBody ConsumableStatusDTO consumableStatus
	) {
		var consumable = consumableStatus.consumable();
		var consumableName = consumable.name();
		var enabled = consumableStatus.enabled();

		var player = consumableService.changeConsumableStatus(playerId, consumableName, enabled);

		log.info("Changed consumable charId: {}, name: {}, enabled: {}", playerId, consumableName, enabled);

		var consumableStatuses = consumableService.getConsumableStatuses(player);

		return consumableStatusConverter.convertList(consumableStatuses);
	}
}

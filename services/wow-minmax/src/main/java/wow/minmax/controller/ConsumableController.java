package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.minmax.client.dto.ConsumableDTO;
import wow.minmax.client.dto.OptionGroupDTO;
import wow.minmax.client.dto.OptionStatusDTO;
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
	public List<OptionGroupDTO<ConsumableDTO>> getConsumables(
			@PathVariable("playerId") PlayerId playerId
	) {
		var consumableStatuses = consumableService.getConsumableStatuses(playerId);

		return consumableStatusConverter.convertAndGroup(consumableStatuses);
	}

	@PutMapping("{playerId}")
	public void enableConsumable(
			@PathVariable("playerId") PlayerId playerId,
			@RequestBody OptionStatusDTO<ConsumableDTO> consumableStatus
	) {
		var consumable = consumableStatus.option();
		var consumableName = consumable.name();
		var enabled = consumableStatus.enabled();

		consumableService.changeConsumableStatus(playerId, consumableName, enabled);

		log.info("Changed consumable charId: {}, name: {}, enabled: {}", playerId, consumableName, enabled);
	}
}

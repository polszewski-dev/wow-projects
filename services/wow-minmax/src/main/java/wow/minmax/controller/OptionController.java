package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.commons.model.buff.BuffId;
import wow.commons.model.item.ConsumableId;
import wow.minmax.client.dto.BuffDTO;
import wow.minmax.client.dto.ConsumableDTO;
import wow.minmax.client.dto.OptionGroupDTO;
import wow.minmax.client.dto.OptionStatusDTO;
import wow.minmax.converter.dto.BuffStatusConverter;
import wow.minmax.converter.dto.ConsumableStatusConverter;
import wow.minmax.model.PlayerId;
import wow.minmax.service.OptionService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-21
 */
@RestController
@RequestMapping("api/v1/options")
@AllArgsConstructor
@Slf4j
public class OptionController {
	private final OptionService optionService;

	private final BuffStatusConverter buffStatusConverter;
	private final ConsumableStatusConverter consumableStatusConverter;

	@GetMapping("{playerId}/buffs")
	public List<OptionGroupDTO<BuffDTO>> getBuffStatuses(
			@PathVariable("playerId") PlayerId playerId
	) {
		var buffStatuses = optionService.getBuffStatuses(playerId);

		return buffStatusConverter.convertAndGroup(buffStatuses);
	}

	@PutMapping("{playerId}/buffs")
	public void changeBuffStatus(
			@PathVariable("playerId") PlayerId playerId,
			@RequestBody OptionStatusDTO<BuffDTO> buffStatus
	) {
		var buffId = BuffId.of(buffStatus.option().id());
		var enabled = buffStatus.enabled();

		optionService.changeBuffStatus(playerId, buffId, enabled);

		log.info("Changed buff charId: {}, buffId: {}, enabled: {}", playerId, buffId, enabled);
	}

	@GetMapping("{playerId}/consumables")
	public List<OptionGroupDTO<ConsumableDTO>> getConsumables(
			@PathVariable("playerId") PlayerId playerId
	) {
		var consumableStatuses = optionService.getConsumableStatuses(playerId);

		return consumableStatusConverter.convertAndGroup(consumableStatuses);
	}

	@PutMapping("{playerId}/consumables")
	public void enableConsumable(
			@PathVariable("playerId") PlayerId playerId,
			@RequestBody OptionStatusDTO<ConsumableDTO> consumableStatus
	) {
		var consumableId = ConsumableId.of(consumableStatus.option().id());
		var enabled = consumableStatus.enabled();

		optionService.changeConsumableStatus(playerId, consumableId, enabled);

		log.info("Changed consumable charId: {}, consumableId: {}, enabled: {}", playerId, consumableId, enabled);
	}
}

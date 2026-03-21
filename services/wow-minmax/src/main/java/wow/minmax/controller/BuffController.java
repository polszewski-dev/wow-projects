package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.commons.model.buff.BuffId;
import wow.minmax.client.dto.BuffDTO;
import wow.minmax.client.dto.OptionGroupDTO;
import wow.minmax.client.dto.OptionStatusDTO;
import wow.minmax.converter.dto.BuffStatusConverter;
import wow.minmax.model.PlayerId;
import wow.minmax.service.BuffService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-31
 */
@RestController
@RequestMapping("api/v1/buffs")
@AllArgsConstructor
@Slf4j
public class BuffController {
	private final BuffService buffService;
	private final BuffStatusConverter buffStatusConverter;

	@GetMapping("{playerId}")
	public List<OptionGroupDTO<BuffDTO>> getBuffStatuses(
			@PathVariable("playerId") PlayerId playerId
	) {
		var buffStatuses = buffService.getBuffStatuses(playerId);

		return buffStatusConverter.convertAndGroup(buffStatuses);
	}

	@PutMapping("{playerId}")
	public void changeBuffStatus(
			@PathVariable("playerId") PlayerId playerId,
			@RequestBody OptionStatusDTO<BuffDTO> buffStatus
	) {
		var buffId = BuffId.of(buffStatus.option().id());
		var enabled = buffStatus.enabled();

		buffService.changeBuffStatus(playerId, buffId, enabled);

		log.info("Changed buff charId: {}, buffId: {}, enabled: {}", playerId, buffId, enabled);
	}
}

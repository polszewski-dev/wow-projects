package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.character.model.character.BuffListType;
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

	@GetMapping("{playerId}/{buffListType}")
	public List<OptionGroupDTO<BuffDTO>> getBuffStatuses(
			@PathVariable("playerId") PlayerId playerId,
			@PathVariable("buffListType") BuffListType buffListType
	) {
		var buffStatuses = buffService.getBuffStatuses(playerId, buffListType);

		return buffStatusConverter.convertAndGroup(buffStatuses);
	}

	@PutMapping("{playerId}/{buffListType}")
	public void changeBuffStatus(
			@PathVariable("playerId") PlayerId playerId,
			@PathVariable("buffListType") BuffListType buffListType,
			@RequestBody OptionStatusDTO<BuffDTO> buffStatus
	) {
		var buffId = BuffId.of(buffStatus.option().id());
		var enabled = buffStatus.enabled();

		buffService.changeBuffStatus(playerId, buffListType, buffId, enabled);

		log.info("Changed buff charId: {}, list: {}, buffId: {}, enabled: {}", playerId, buffListType, buffId, enabled);
	}
}

package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.character.model.character.BuffListType;
import wow.minmax.client.dto.BuffStatusDTO;
import wow.minmax.converter.dto.BuffStatusConverter;
import wow.minmax.model.CharacterId;
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

	@GetMapping("{characterId}/{buffListType}")
	public List<BuffStatusDTO> getBuffStatuses(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("buffListType") BuffListType buffListType
	) {
		var buffStatuses = buffService.getBuffStatuses(characterId, buffListType);

		return buffStatusConverter.convertList(buffStatuses);
	}

	@PutMapping("{characterId}/{buffListType}")
	public List<BuffStatusDTO> changeBuffStatus(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("buffListType") BuffListType buffListType,
			@RequestBody BuffStatusDTO buffStatus
	) {
		var buffId = buffStatus.buff().buffId();
		var rank = buffStatus.buff().rank();
		var enabled = buffStatus.enabled();

		var player = buffService.changeBuffStatus(characterId, buffListType, buffId, rank, enabled);

		log.info("Changed buff charId: {}, list: {}, buffId: {}, rank: {}, enabled: {}", characterId, buffListType, buffId, rank, enabled);

		var buffStatuses = buffService.getBuffStatuses(player, buffListType);

		return buffStatusConverter.convertList(buffStatuses);
	}
}

package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.minmax.client.dto.TalentDTO;
import wow.minmax.converter.dto.TalentConverter;
import wow.minmax.model.PlayerId;
import wow.minmax.service.PlayerService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-31
 */
@RestController
@RequestMapping("api/v1/talents")
@AllArgsConstructor
@Slf4j
public class TalentController {
	private final PlayerService playerService;
	private final TalentConverter talentConverter;

	@GetMapping("{playerId}")
	public List<TalentDTO> getTalents(
			@PathVariable("playerId") PlayerId playerId
	) {
		var player = playerService.getPlayer(playerId);
		var talents = player.getTalents().getList();

		return talentConverter.convertList(talents);
	}
}

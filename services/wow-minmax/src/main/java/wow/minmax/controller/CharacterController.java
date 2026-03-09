package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.minmax.client.dto.*;
import wow.minmax.converter.dto.ExclusiveFactionGroupConverter;
import wow.minmax.converter.dto.PlayerInfoConverter;
import wow.minmax.converter.dto.ProfessionConverter;
import wow.minmax.converter.dto.ScriptInfoConverter;
import wow.minmax.model.PlayerId;
import wow.minmax.service.PlayerService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-04
 */
@RestController
@RequestMapping("api/v1/characters")
@AllArgsConstructor
@Slf4j
public class CharacterController {
	private final PlayerService playerService;
	private final PlayerInfoConverter playerInfoConverter;
	private final ProfessionConverter professionConverter;
	private final ExclusiveFactionGroupConverter exclusiveFactionGroupConverter;
	private final ScriptInfoConverter scriptInfoConverter;

	@GetMapping("{playerId}")
	public PlayerInfoDTO getCharacter(
			@PathVariable("playerId") PlayerId playerId
	) {
		var player = playerService.getPlayer(playerId);

		return playerInfoConverter.convert(player, playerId);
	}

	@GetMapping("{playerId}/professions")
	public List<ProfessionDTO> getAvailableProfessions(
			@PathVariable("playerId") PlayerId playerId
	) {
		var availableProfessions = playerService.getAvailableProfessions(playerId);

		return professionConverter.convertList(availableProfessions);
	}

	@PutMapping("{playerId}/professions/{index}")
	public PlayerInfoDTO changeProfession(
			@PathVariable("playerId") PlayerId playerId,
			@PathVariable("index") int index,
			@RequestBody ProfessionDTO profession
	) {
		var player = playerService.changeProfession(
				playerId,
				index,
				professionConverter.convertBack(profession)
		);

		log.info("changed profession charId: {}, idx: {}, profession: {}", playerId, index, profession.name());

		return playerInfoConverter.convert(player, playerId);
	}

	@GetMapping("{playerId}/xfactions")
	public List<ExclusiveFactionGroupDTO> getAvailableExclusiveFactions(
			@PathVariable("playerId") PlayerId playerId
	) {
		var availableExclusiveFactions = playerService.getAvailableExclusiveFactions(playerId);

		return exclusiveFactionGroupConverter.convertList(availableExclusiveFactions);
	}

	@PutMapping("{playerId}/xfactions")
	public void changeExclusiveFaction(
			@PathVariable("playerId") PlayerId playerId,
			@RequestBody ExclusiveFactionDTO exclusiveFaction
	) {
		playerService.changeExclusiveFaction(
				playerId,
				exclusiveFaction.name()
		);

		log.info("changed xfaction charId: {}, xfaction: {}", playerId, exclusiveFaction.name());
	}

	@PutMapping("{playerId}/talents")
	public PlayerInfoDTO changeTalents(
			@PathVariable("playerId") PlayerId playerId,
			@RequestBody String talentLink
	) {
		var player = playerService.changeTalents(
				playerId,
				talentLink
		);

		log.info("changed talents charId: {}, link: {}", playerId, talentLink);

		return playerInfoConverter.convert(player, playerId);
	}

	@GetMapping("{playerId}/scripts")
	public List<ScriptInfoDTO> getAvailableScripts(
			@PathVariable("playerId") PlayerId playerId
	) {
		var availableScripts = playerService.getAvailableScripts(playerId);

		return scriptInfoConverter.convertList(availableScripts);
	}

	@PutMapping("{playerId}/scripts")
	public PlayerInfoDTO changeScript(
			@PathVariable("playerId") PlayerId playerId,
			@RequestBody ScriptInfoDTO script
	) {
		var player = playerService.changeScript(
				playerId,
				script.id()
		);

		log.info("changed script charId: {}, script: {}", playerId, script.id());

		return playerInfoConverter.convert(player, playerId);
	}
}

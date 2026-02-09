package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.minmax.client.dto.*;
import wow.minmax.converter.dto.ExclusiveFactionGroupConverter;
import wow.minmax.converter.dto.PlayerInfoConverter;
import wow.minmax.converter.dto.ProfessionConverter;
import wow.minmax.converter.dto.ScriptInfoConverter;
import wow.minmax.model.CharacterId;
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

	@GetMapping("{characterId}")
	public PlayerInfoDTO getCharacter(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerService.getPlayer(characterId);

		return playerInfoConverter.convert(player, characterId);
	}

	@GetMapping("{characterId}/professions")
	public List<ProfessionDTO> getAvailableProfessions(
			@PathVariable("characterId") CharacterId characterId
	) {
		var availableProfessions = playerService.getAvailableProfessions(characterId);

		return professionConverter.convertList(availableProfessions);
	}

	@PutMapping("{characterId}/professions/{index}")
	public PlayerInfoDTO changeProfession(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("index") int index,
			@RequestBody ProfessionDTO profession
	) {
		var player = playerService.changeProfession(
				characterId,
				index,
				professionConverter.convertBack(profession)
		);

		log.info("changed profession charId: {}, idx: {}, profession: {}", characterId, index, profession.name());

		return playerInfoConverter.convert(player, characterId);
	}

	@GetMapping("{characterId}/xfactions")
	public List<ExclusiveFactionGroupDTO> getAvailableExclusiveFactions(
			@PathVariable("characterId") CharacterId characterId
	) {
		var availableExclusiveFactions = playerService.getAvailableExclusiveFactions(characterId);

		return exclusiveFactionGroupConverter.convertList(availableExclusiveFactions);
	}

	@PutMapping("{characterId}/xfactions")
	public void changeExclusiveFaction(
			@PathVariable("characterId") CharacterId characterId,
			@RequestBody ExclusiveFactionDTO exclusiveFaction
	) {
		playerService.changeExclusiveFaction(
				characterId,
				exclusiveFaction.name()
		);

		log.info("changed xfaction charId: {}, xfaction: {}", characterId, exclusiveFaction.name());
	}

	@PutMapping("{characterId}/talents")
	public PlayerInfoDTO changeTalents(
			@PathVariable("characterId") CharacterId characterId,
			@RequestBody String talentLink
	) {
		var player = playerService.changeTalents(
				characterId,
				talentLink
		);

		log.info("changed talents charId: {}, link: {}", characterId, talentLink);

		return playerInfoConverter.convert(player, characterId);
	}

	@GetMapping("{characterId}/scripts")
	public List<ScriptInfoDTO> getAvailableScripts(
			@PathVariable("characterId") CharacterId characterId
	) {
		var availableScripts = playerService.getAvailableScripts(characterId);

		return scriptInfoConverter.convertList(availableScripts);
	}

	@PutMapping("{characterId}/scripts")
	public PlayerInfoDTO changeScript(
			@PathVariable("characterId") CharacterId characterId,
			@RequestBody ScriptInfoDTO script
	) {
		var player = playerService.changeScript(
				characterId,
				script.id()
		);

		log.info("changed script charId: {}, script: {}", characterId, script.id());

		return playerInfoConverter.convert(player, characterId);
	}
}

package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.minmax.client.dto.*;
import wow.minmax.converter.dto.*;
import wow.minmax.model.CharacterId;
import wow.minmax.service.PlayerCharacterService;

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
	private final PlayerCharacterService playerCharacterService;
	private final PlayerCharacterConverter playerCharacterConverter;
	private final ProfessionConverter professionConverter;
	private final ExclusiveFactionConverter exclusiveFactionConverter;
	private final ExclusiveFactionGroupConverter exclusiveFactionGroupConverter;
	private final ScriptInfoConverter scriptInfoConverter;

	@GetMapping("{characterId}")
	public PlayerCharacterDTO getCharacter(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);

		return playerCharacterConverter.convert(player, characterId);
	}

	@GetMapping("{characterId}/professions")
	public List<ProfessionDTO> getAvailableProfessions(
			@PathVariable("characterId") CharacterId characterId
	) {
		var availableProfessions = playerCharacterService.getAvailableProfessions(characterId);

		return professionConverter.convertList(availableProfessions);
	}

	@PutMapping("{characterId}/professions/{index}")
	public PlayerCharacterDTO changeProfession(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("index") int index,
			@RequestBody ProfessionDTO profession
	) {
		var player = playerCharacterService.changeProfession(
				characterId,
				index,
				professionConverter.convertBack(profession)
		);

		log.info("changed profession charId: {}, idx: {}, profession: {}", characterId, index, profession.name());

		return playerCharacterConverter.convert(player, characterId);
	}

	@GetMapping("{characterId}/xfactions")
	public List<ExclusiveFactionGroupDTO> getAvailableExclusiveFactions(
			@PathVariable("characterId") CharacterId characterId
	) {
		var availableExclusiveFactions = playerCharacterService.getAvailableExclusiveFactions(characterId);

		return exclusiveFactionGroupConverter.convertList(availableExclusiveFactions);
	}

	@PutMapping("{characterId}/xfactions")
	public void changeExclusiveFaction(
			@PathVariable("characterId") CharacterId characterId,
			@RequestBody ExclusiveFactionDTO exclusiveFaction
	) {
		playerCharacterService.changeExclusiveFaction(
				characterId,
				exclusiveFaction.name()
		);

		log.info("changed xfaction charId: {}, xfaction: {}", characterId, exclusiveFaction.name());
	}

	@GetMapping("{characterId}/scripts")
	public List<ScriptInfoDTO> getAvailableScripts(
			@PathVariable("characterId") CharacterId characterId
	) {
		var availableScripts = playerCharacterService.getAvailableScripts(characterId);

		return scriptInfoConverter.convertList(availableScripts);
	}

	@PutMapping("{characterId}/scripts")
	public PlayerCharacterDTO changeScript(
			@PathVariable("characterId") CharacterId characterId,
			@RequestBody ScriptInfoDTO script
	) {
		var player = playerCharacterService.changeScript(
				characterId,
				script.id()
		);

		log.info("changed script charId: {}, script: {}", characterId, script.id());

		return playerCharacterConverter.convert(player, characterId);
	}
}

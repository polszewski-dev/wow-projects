package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.minmax.converter.dto.PlayerCharacterConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.dto.PlayerCharacterDTO;
import wow.minmax.service.PlayerProfileService;

/**
 * User: POlszewski
 * Date: 2023-04-04
 */
@RestController
@RequestMapping("api/v1/characters")
@AllArgsConstructor
@Slf4j
public class CharacterController {
	private final PlayerProfileService playerProfileService;
	private final PlayerCharacterConverter playerCharacterConverter;

	@GetMapping("{characterId}")
	public PlayerCharacterDTO getCharacter(
			@PathVariable("characterId") CharacterId characterId
	) {
		var character = playerProfileService.getCharacter(characterId);
		var dto = playerCharacterConverter.convert(character);

		dto.setCharacterId(characterId.toString());
		return dto;
	}
}

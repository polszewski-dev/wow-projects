package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.model.character.Character;
import wow.minmax.converter.dto.CharacterConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.dto.CharacterDTO;
import wow.minmax.service.PlayerProfileService;

/**
 * User: POlszewski
 * Date: 2023-04-04
 */
@RestController
@RequestMapping("api/v1/character")
@AllArgsConstructor
@Slf4j
public class CharacterController {
	private final PlayerProfileService playerProfileService;
	private final CharacterConverter characterConverter;

	@GetMapping("{characterId}")
	public CharacterDTO getCharacter(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.getCharacter(characterId);
		CharacterDTO dto = characterConverter.convert(character);

		dto.setCharacterId(characterId.toString());
		return dto;
	}
}

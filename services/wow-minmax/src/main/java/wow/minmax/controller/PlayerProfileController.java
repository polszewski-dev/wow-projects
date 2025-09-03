package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.minmax.client.dto.CharacterSelectionOptionsDTO;
import wow.minmax.client.dto.NewProfileOptionsDTO;
import wow.minmax.client.dto.PlayerProfileInfoDTO;
import wow.minmax.converter.dto.CharacterSelectionOptionsConverter;
import wow.minmax.converter.dto.NewProfileOptionsConverter;
import wow.minmax.converter.dto.PlayerProfileInfoConverter;
import wow.minmax.service.PlayerProfileService;

import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@RestController
@RequestMapping("api/v1/profiles")
@AllArgsConstructor
@Slf4j
public class PlayerProfileController {
	private final PlayerProfileService playerProfileService;

	private final PlayerProfileInfoConverter playerProfileInfoConverter;
	private final NewProfileOptionsConverter newProfileOptionsConverter;
	private final CharacterSelectionOptionsConverter characterSelectionOptionsConverter;

	@GetMapping
	public List<PlayerProfileInfoDTO> getPlayerProfileList() {
		var playerProfileInfos = playerProfileService.getPlayerProfileInfos();

		return playerProfileInfoConverter.convertList(playerProfileInfos);
	}

	@PostMapping
	public PlayerProfileInfoDTO createPlayerProfile(
			@RequestBody PlayerProfileInfoDTO playerProfileInfoDTO
	) {
		var playerProfileInfo = playerProfileInfoConverter.convertBack(playerProfileInfoDTO);

		var createdPlayerProfile = playerProfileService.createPlayerProfile(playerProfileInfo);

		log.info("Created profile id: {}, name: {}", createdPlayerProfile.getProfileId(), createdPlayerProfile.getProfileName());

		return playerProfileInfoConverter.convert(createdPlayerProfile.getProfileInfo());
	}

	@GetMapping("new-options")
	public NewProfileOptionsDTO getNewProfileOptions() {
		var newProfileOptions = playerProfileService.getNewProfileOptions();

		return newProfileOptionsConverter.convert(newProfileOptions);
	}

	@GetMapping("{profileId}/char-selection-options")
	public CharacterSelectionOptionsDTO getCharacterSelectionOptions(
			@PathVariable("profileId") UUID profileId
	) {
		var characterSelectionOptions = playerProfileService.getCharacterSelectionOptions(profileId);

		return characterSelectionOptionsConverter.convert(characterSelectionOptions);
	}
}

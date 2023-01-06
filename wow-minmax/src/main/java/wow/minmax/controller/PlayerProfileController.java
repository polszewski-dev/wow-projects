package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.minmax.converter.dto.PlayerProfileInfoConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.dto.PlayerProfileInfoDTO;
import wow.minmax.service.PlayerProfileService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@RestController
@RequestMapping("api/v1/profile")
@AllArgsConstructor
@Slf4j
public class PlayerProfileController {
	private final PlayerProfileService playerProfileService;
	private final PlayerProfileInfoConverter playerProfileInfoConverter;

	@GetMapping("list")
	public List<PlayerProfileInfoDTO> getPlayerProfileList() {
		List<PlayerProfileInfo> playerProfileInfos = playerProfileService.getPlayerProfileInfos();
		return playerProfileInfoConverter.convertList(playerProfileInfos);
	}

	@PostMapping
	public PlayerProfileInfoDTO createPlayerProfile(
			@RequestBody PlayerProfileInfoDTO playerProfileInfoDTO
	) {
		PlayerProfileInfo playerProfileInfo = playerProfileInfoConverter.convertBack(playerProfileInfoDTO);
		PlayerProfile createdPlayerProfile = playerProfileService.createPlayerProfile(playerProfileInfo);
		log.info("Created profile id: {}, name: {}", createdPlayerProfile.getProfileId(), createdPlayerProfile.getProfileName());
		return playerProfileInfoConverter.convert(createdPlayerProfile.getProfileInfo());
	}
}

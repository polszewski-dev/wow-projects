package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.pve.Phase;
import wow.minmax.converter.dto.BuffConverter;
import wow.minmax.converter.dto.PlayerProfileConverter;
import wow.minmax.converter.dto.PlayerProfileInfoConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.dto.BuffDTO;
import wow.minmax.model.dto.PlayerProfileDTO;
import wow.minmax.model.dto.PlayerProfileInfoDTO;
import wow.minmax.service.PlayerProfileService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	private final PlayerProfileConverter playerProfileConverter;
	private final PlayerProfileInfoConverter playerProfileInfoConverter;
	private final BuffConverter buffConverter;

	@GetMapping("list")
	public List<PlayerProfileInfoDTO> getPlayerProfileList() {
		List<PlayerProfileInfo> playerProfileInfos = playerProfileService.getPlayerProfileInfos();
		return playerProfileInfoConverter.convertList(playerProfileInfos);
	}

	@GetMapping("{profileId}")
	public PlayerProfileDTO getPlayerProfile(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);
		return playerProfileConverter.convert(playerProfile);
	}

	@GetMapping("create/name/{profileName}/phase/{phase}")
	public PlayerProfileDTO createPlayerProfile(
			@PathVariable("profileName") String profileName,
			@PathVariable("phase") Phase phase
	) {
		PlayerProfile createdProfile = playerProfileService.createPlayerProfile(profileName, phase);
		PlayerProfileDTO createdProfileDTO = playerProfileConverter.convert(createdProfile);

		log.info("Created profile id: {}, name: {}", createdProfile.getProfileId(), createdProfile.getProfileName());
		return createdProfileDTO;
	}

	@GetMapping("copy/{copiedProfileId}/name/{profileName}/phase/{phase}")
	public PlayerProfileDTO createPlayerProfile(
			@PathVariable("copiedProfileId") UUID copiedProfileId,
			@PathVariable("profileName") String profileName,
			@PathVariable("phase") Phase phase
	) {
		PlayerProfile createdProfile = playerProfileService.copyPlayerProfile(copiedProfileId, profileName, phase);
		PlayerProfileDTO createdProfileDTO = playerProfileConverter.convert(createdProfile);

		log.info("Copied profile id: {}, name: {}, sourceId: {}", createdProfile.getProfileId(), createdProfile.getProfileName(), copiedProfileId);
		return createdProfileDTO;
	}

	@GetMapping("{profileId}/enable/buff/{buffId}/{enabled}")
	public List<BuffDTO> enableBuff(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("buffId") int buffId,
			@PathVariable("enabled") boolean enabled) {
		PlayerProfile playerProfile = playerProfileService.enableBuff(profileId, buffId, enabled);
		log.info("Changed buff profile id: {}, buffId: {}, enabled: {}", profileId, buffId, enabled);
		return buffConverter.convertList(new ArrayList<>(playerProfile.getBuffs().getList()));
	}
}

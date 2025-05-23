package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import wow.commons.client.converter.CharacterClassConverter;
import wow.commons.client.converter.PhaseConverter;
import wow.commons.client.dto.EnemyTypeDTO;
import wow.commons.client.dto.LevelDifferenceDTO;
import wow.commons.client.dto.PhaseDTO;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.pve.GameVersionRepository;
import wow.minmax.client.dto.CharacterSelectionOptionsDTO;
import wow.minmax.client.dto.NewProfileOptionsDTO;
import wow.minmax.client.dto.PlayerProfileInfoDTO;
import wow.minmax.config.ProfileConfig;
import wow.minmax.converter.dto.PlayerProfileInfoConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.PlayerProfileService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

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
	private final GameVersionRepository gameVersionRepository;

	private final PlayerProfileInfoConverter playerProfileInfoConverter;
	private final CharacterClassConverter characterClassConverter;
	private final PhaseConverter phaseConverter;

	private final ProfileConfig profileConfig;

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
		var latestSupportedVersionId = profileConfig.getLatestSupportedVersionId();
		var gameVersion = gameVersionRepository.getGameVersion(latestSupportedVersionId).orElseThrow();

		var supportedClasses = profileConfig.getSupportedClasses().stream()
				.map(gameVersion::getCharacterClass)
				.map(Optional::orElseThrow)
				.map(characterClassConverter::convert)
				.toList();

		return new NewProfileOptionsDTO(supportedClasses);
	}

	@GetMapping("{profileId}/char-selection-options")
	public CharacterSelectionOptionsDTO getCharacterSelectionOptions(
			@PathVariable("profileId") UUID profileId
	) {
		var playerProfile = playerProfileService.getPlayerProfile(profileId);

		return new CharacterSelectionOptionsDTO(
				getPhases(playerProfile),
				getEnemyTypes(),
				getEnemyLevelDifferences()
		);
	}

	private List<PhaseDTO> getPhases(PlayerProfile playerProfile) {
		return Stream.of(GameVersionId.values())
				.filter(x -> x != GameVersionId.WOTLK)//not supported atm
				.map(x -> gameVersionRepository.getGameVersion(x).orElseThrow())
				.filter(x -> x.supports(playerProfile.getCharacterClassId(), playerProfile.getRaceId()))
				.flatMap(x -> x.getPhases().stream())
				.map(phaseConverter::convert)
				.toList();
	}

	private List<EnemyTypeDTO> getEnemyTypes() {
		return Stream.of(CreatureType.values())
				.map(x -> new EnemyTypeDTO(x.toString(), StringUtils.capitalize(x.toString())))
				.toList();
	}

	private List<LevelDifferenceDTO> getEnemyLevelDifferences() {
		return List.of(
				new LevelDifferenceDTO(0, "+0"),
				new LevelDifferenceDTO(1, "+1"),
				new LevelDifferenceDTO(2, "+2"),
				new LevelDifferenceDTO(3, "+3 (Boss)")
		);
	}
}

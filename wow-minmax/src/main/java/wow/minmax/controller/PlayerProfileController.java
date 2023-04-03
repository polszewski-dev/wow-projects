package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import wow.character.model.character.GameVersion;
import wow.character.repository.CharacterRepository;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.GameVersionId;
import wow.minmax.config.ProfileConfig;
import wow.minmax.converter.dto.CharacterClassConverter;
import wow.minmax.converter.dto.PhaseConverter;
import wow.minmax.converter.dto.PlayerProfileInfoConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.dto.*;
import wow.minmax.service.PlayerProfileService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

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
	private final CharacterRepository characterRepository;

	private final PlayerProfileInfoConverter playerProfileInfoConverter;
	private final CharacterClassConverter characterClassConverter;
	private final PhaseConverter phaseConverter;

	private final ProfileConfig profileConfig;

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

	@GetMapping("new/options")
	public NewProfileOptionsDTO getNewProfileOptions() {
		GameVersionId latestSupportedVersionId = profileConfig.getLatestSupportedVersionId();
		GameVersion gameVersion = characterRepository.getGameVersion(latestSupportedVersionId).orElseThrow();

		var supportedClasses = profileConfig.getSupportedClasses().stream()
				.map(gameVersion::getCharacterClass)
				.map(characterClassConverter::convert)
				.toList();

		return new NewProfileOptionsDTO(supportedClasses);
	}

	@GetMapping("{profileId}/char/selection/options")
	public CharacterSelectionOptionsDTO getCharacterSelectionOptions(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);

		return new CharacterSelectionOptionsDTO(
				getPhases(playerProfile),
				getEnemyTypes(),
				getEnemyLevelDifferences(),
				playerProfile.getLastModifiedCharacterId().toString()
		);
	}

	private List<PhaseDTO> getPhases(PlayerProfile playerProfile) {
		return Stream.of(GameVersionId.values())
				.map(x -> characterRepository.getGameVersion(x).orElseThrow())
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

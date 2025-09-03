package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Phase;
import wow.commons.repository.pve.GameVersionRepository;
import wow.minmax.config.ProfileConfig;
import wow.minmax.model.*;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerProfileService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Service
@AllArgsConstructor
public class PlayerProfileServiceImpl implements PlayerProfileService {
	private final GameVersionRepository gameVersionRepository;
	private final PlayerProfileRepository playerProfileRepository;

	private final ProfileConfig profileConfig;

	@Override
	public List<PlayerProfileInfo> getPlayerProfileInfos() {
		return playerProfileRepository.findAll().stream()
				.map(PlayerProfile::getProfileInfo)
				.toList();
	}

	@Override
	public PlayerProfile createPlayerProfile(PlayerProfileInfo playerProfileInfo) {
		var profileId = UUID.randomUUID();

		var playerProfile = new PlayerProfile(
				profileId.toString(),
				playerProfileInfo.getProfileName(),
				playerProfileInfo.getCharacterClassId(),
				playerProfileInfo.getRaceId(),
				LocalDateTime.now(),
				getDefaultCharacterId(profileId).toString()
		);

		saveProfile(playerProfile);
		return playerProfile;
	}

	private CharacterId getDefaultCharacterId(UUID profileId) {
		var latestSupportedVersionId = profileConfig.getLatestSupportedVersionId();
		var latestSupportedVersion = gameVersionRepository.getGameVersion(latestSupportedVersionId).orElseThrow();

		var defaultPhase = latestSupportedVersion.getLastPhase();

		return new CharacterId(
				profileId,
				defaultPhase.getPhaseId(),
				defaultPhase.getMaxLevel(),
				profileConfig.getDefaultEnemyType(),
				profileConfig.getDefaultLevelDiff()
		);
	}

	@Override
	public PlayerProfile getPlayerProfile(UUID profileId) {
		return playerProfileRepository.findById(profileId.toString()).orElseThrow();
	}

	private void saveProfile(PlayerProfile playerProfile) {
		playerProfileRepository.save(playerProfile);
	}

	@Override
	public NewProfileOptions getNewProfileOptions() {
		var latestSupportedVersionId = profileConfig.getLatestSupportedVersionId();
		var gameVersion = gameVersionRepository.getGameVersion(latestSupportedVersionId).orElseThrow();

		var supportedClasses = profileConfig.getSupportedClasses().stream()
				.map(gameVersion::getCharacterClass)
				.map(Optional::orElseThrow)
				.toList();

		return new NewProfileOptions(supportedClasses);
	}

	@Override
	public CharacterSelectionOptions getCharacterSelectionOptions(UUID profileId) {
		var playerProfile = getPlayerProfile(profileId);

		return new CharacterSelectionOptions(
				getPhases(playerProfile),
				getEnemyTypes(),
				getEnemyLevelDifferences()
		);
	}

	private List<Phase> getPhases(PlayerProfile playerProfile) {
		return Stream.of(GameVersionId.values())
				.filter(x -> x != GameVersionId.WOTLK)//not supported atm
				.map(x -> gameVersionRepository.getGameVersion(x).orElseThrow())
				.filter(x -> x.supports(playerProfile.getCharacterClassId(), playerProfile.getRaceId()))
				.flatMap(x -> x.getPhases().stream())
				.toList();
	}

	private List<CreatureType> getEnemyTypes() {
		return List.of(CreatureType.values());
	}

	private List<Integer> getEnemyLevelDifferences() {
		return List.of(0, 1, 2, 3);
	}
}

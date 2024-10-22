package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.repository.pve.GameVersionRepository;
import wow.minmax.config.ProfileConfig;
import wow.minmax.converter.persistent.PlayerProfilePOConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerProfileService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Service
@AllArgsConstructor
public class PlayerProfileServiceImpl implements PlayerProfileService {
	private final GameVersionRepository gameVersionRepository;
	private final PlayerProfileRepository playerProfileRepository;
	private final PlayerProfilePOConverter playerProfilePOConverter;

	private final ProfileConfig profileConfig;

	@Override
	public List<PlayerProfileInfo> getPlayerProfileInfos() {
		return playerProfileRepository.findAll().stream()
				.map(this::getPlayerProfile)
				.map(PlayerProfile::getProfileInfo)
				.toList();
	}

	@Override
	public PlayerProfile createPlayerProfile(PlayerProfileInfo playerProfileInfo) {
		var profileId = UUID.randomUUID();

		var playerProfile = new PlayerProfile(
				profileId,
				playerProfileInfo.getProfileName(),
				playerProfileInfo.getCharacterClassId(),
				playerProfileInfo.getRaceId(),
				LocalDateTime.now(),
				getDefaultCharacterId(profileId)
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
		return getPlayerProfile(playerProfileRepository.findById(profileId.toString()).orElseThrow());
	}

	private void saveProfile(PlayerProfile playerProfile) {
		var playerProfilePO = playerProfilePOConverter.convert(playerProfile);

		playerProfileRepository.save(playerProfilePO);
	}

	private PlayerProfile getPlayerProfile(PlayerProfilePO profile) {
		return playerProfilePOConverter.convertBack(profile);
	}
}

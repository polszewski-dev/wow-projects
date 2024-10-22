package wow.minmax.service;

import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;

import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public interface PlayerProfileService {
	List<PlayerProfileInfo> getPlayerProfileInfos();

	PlayerProfile createPlayerProfile(PlayerProfileInfo playerProfileInfo);

	PlayerProfile getPlayerProfile(UUID profileId);
}

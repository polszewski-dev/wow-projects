package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.service.PlayerProfileService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
abstract class ControllerTest extends WowMinMaxSpringTest {
	@MockBean
	PlayerProfileService playerProfileService;

	PlayerProfile profile;
	PlayerProfileInfo profileInfo;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile();
		profile.setEquipment(getEquipment());

		profileInfo = new PlayerProfileInfo(
				profile.getProfileId(),
				profile.getProfileName(),
				profile.getCharacterClass(),
				profile.getRace(),
				profile.getLevel(),
				profile.getEnemyType(),
				profile.getBuildId(),
				profile.getPhase(),
				profile.getLastModified()
		);

		when(playerProfileService.getPlayerProfileInfos()).thenReturn(List.of(profileInfo));
		when(playerProfileService.getPlayerProfile(profile.getProfileId())).thenReturn(profile);
		when(playerProfileService.createPlayerProfile(any(), any())).thenReturn(profile);
		when(playerProfileService.copyPlayerProfile(any(), any(), any())).thenReturn(profile);
		when(playerProfileService.resetEquipment(any())).thenReturn(profile);
		when(playerProfileService.changeItemBestVariant(any(), any(), anyInt())).thenReturn(profile);
		when(playerProfileService.changeItem(any(), any(), any())).thenReturn(profile);
		when(playerProfileService.changeItemGroup(any(), any(), any())).thenReturn(profile);
		when(playerProfileService.enableBuff(any(), anyInt(), anyBoolean())).thenReturn(profile);
	}
}

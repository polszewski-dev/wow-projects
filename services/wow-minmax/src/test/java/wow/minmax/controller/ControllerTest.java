package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import wow.character.model.character.PlayerCharacter;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestriction;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.config.ViewConfig;
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
	PlayerCharacter character;
	PlayerProfileInfo profileInfo;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile();
		character = profile.getCharacter(CHARACTER_KEY).orElseThrow();

		character.setEquipment(getEquipment());

		profileInfo = profile.getProfileInfo();

		when(playerProfileService.getPlayerProfileInfos()).thenReturn(List.of(profileInfo));
		when(playerProfileService.getPlayerProfile(profile.getProfileId())).thenReturn(profile);
		when(playerProfileService.getCharacter(CHARACTER_KEY)).thenReturn(character);
		when(playerProfileService.createPlayerProfile(any())).thenReturn(profile);
		when(playerProfileService.resetEquipment(any())).thenReturn(character);
		when(playerProfileService.equipItem(any(), any(), any())).thenReturn(character);
		when(playerProfileService.equipItem(any(), any(), any(), anyBoolean())).thenReturn(character);
		when(playerProfileService.equipItemGroup(any(), any(), any())).thenReturn(character);
		when(playerProfileService.enableBuff(any(), any(), any(), anyInt(), anyBoolean())).thenReturn(character);
		when(playerProfileService.getViewConfig(any())).thenReturn(new ViewConfig(CharacterRestriction.EMPTY, TimeRestriction.of(PHASE), 1, List.of()));
	}
}

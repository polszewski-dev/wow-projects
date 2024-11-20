package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestriction;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.PlayerCharacter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.service.PlayerCharacterService;
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

	@MockBean
	PlayerCharacterService playerCharacterService;

	PlayerProfile profile;
	PlayerCharacter character;
	PlayerProfileInfo profileInfo;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile();
		character = getCharacter();

		character.setEquipment(getEquipment());

		profileInfo = profile.getProfileInfo();

		when(playerProfileService.getPlayerProfileInfos()).thenReturn(List.of(profileInfo));
		when(playerProfileService.getPlayerProfile(profile.getProfileId())).thenReturn(profile);
		when(playerProfileService.createPlayerProfile(any())).thenReturn(profile);
		when(playerCharacterService.getCharacter(CHARACTER_KEY)).thenReturn(character);
		when(playerCharacterService.resetEquipment(any())).thenReturn(character);
		when(playerCharacterService.equipItem(any(), any(), any())).thenReturn(character);
		when(playerCharacterService.equipItem(any(), any(), any(), anyBoolean())).thenReturn(character);
		when(playerCharacterService.equipItemGroup(any(), any(), any())).thenReturn(character);
		when(playerCharacterService.enableBuff(any(), any(), any(), anyInt(), anyBoolean())).thenReturn(character);
		when(playerCharacterService.getViewConfig(any())).thenReturn(new ViewConfig(CharacterRestriction.EMPTY, TimeRestriction.of(PHASE), 1, List.of()));
	}
}

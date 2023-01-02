package wow.minmax.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.commons.model.pve.Phase;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * User: POlszewski
 * Date: 2022-10-19
 */
@WebMvcTest(PlayerProfileController.class)
class PlayerProfileControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	void getPlayerProfileList() throws Exception {
		mockMvc.perform(get("/api/v1/profile/list"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].profileId", is(profile.getProfileId().toString())))
				.andExpect(jsonPath("$[0].profileName", is(profile.getProfileName())))
		;

		verify(playerProfileService).getPlayerProfileInfos();
	}

	@Test
	void getPlayerProfileTest() throws Exception {
		mockMvc.perform(get("/api/v1/profile/{profileId}", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.profileId", is(profile.getProfileId().toString())))
				.andExpect(jsonPath("$.profileName", is(profile.getProfileName())))
		;

		verify(playerProfileService).getPlayerProfile(profile.getProfileId());
	}

	@Test
	void createPlayerProfile() throws Exception {
		String profileName = "testProfile";
		Phase phase = Phase.TBC_P5;

		mockMvc.perform(get("/api/v1/profile/create/name/{profileName}/phase/{phase}", profileName, phase))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).createPlayerProfile(profileName, phase);
	}

	@Test
	void copyPlayerProfile() throws Exception {
		String profileName = "testProfileCopy";
		Phase phase = Phase.TBC_P5;

		mockMvc.perform(get("/api/v1/profile/copy/{copiedProfileId}/name/{profileName}/phase/{phase}", profile.getProfileId(), profileName, phase))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).copyPlayerProfile(profile.getProfileId(), profileName, phase);
	}
}
package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.minmax.service.PlayerProfileService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
@WebMvcTest(StatsController.class)
class StatsControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	PlayerProfileService playerProfileService;

	@Test
	void getSpellStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/spell/{profileId}", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getPlayerStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/player/{profileId}", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getSpecialAbilityStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/special/{profileId}", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		when(playerProfileService.getPlayerProfile(profile.getProfileId())).thenReturn(profile);
	}
}
package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.service.PlayerProfileService;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
@WebMvcTest(UpgradeController.class)
class UpgradeControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	PlayerProfileService playerProfileService;

	@Test
	void findUpgrades() throws Exception {
		mockMvc.perform(get("/api/v1/upgrade/{profileId}/slot/{slotGroup}", profile.getProfileId(), ItemSlotGroup.CHEST))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(1)))
		;

		verify(playerProfileService).getPlayerProfile(profile.getProfileId());
	}

	@Test
	void findAllUpgrades() throws Exception {
		mockMvc.perform(get("/api/v1/upgrade/{profileId}", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).getPlayerProfile(profile.getProfileId());
	}

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		when(playerProfileService.getPlayerProfile(profile.getProfileId())).thenReturn(profile);
	}
}
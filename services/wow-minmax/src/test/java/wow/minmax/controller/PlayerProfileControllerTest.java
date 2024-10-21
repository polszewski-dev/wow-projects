package wow.minmax.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.minmax.converter.dto.PlayerProfileInfoConverter;
import wow.minmax.model.dto.PlayerProfileInfoDTO;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * User: POlszewski
 * Date: 2022-10-19
 */
@WebMvcTest(PlayerProfileController.class)
class PlayerProfileControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	PlayerProfileInfoConverter profileInfoConverter;

	@Test
	void getPlayerProfileList() throws Exception {
		mockMvc.perform(get("/api/v1/profiles"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].profileId", is(profile.getProfileId().toString())))
				.andExpect(jsonPath("$[0].profileName", is(profile.getProfileName())))
		;

		verify(playerProfileService).getPlayerProfileInfos();
	}

	@Test
	void createPlayerProfile() throws Exception {
		PlayerProfileInfoDTO convert = profileInfoConverter.convert(profileInfo);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		String requestBody = objectMapper.writeValueAsString(convert);

		mockMvc.perform(
					post("/api/v1/profiles", requestBody)
							.contentType(MediaType.APPLICATION_JSON)
							.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).createPlayerProfile(any());
	}

	@Test
	void getNewProfileOptions() throws Exception {
		mockMvc.perform(get("/api/v1/profiles/new-options"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void getCharacterSelectionOptions() throws Exception {
		mockMvc.perform(get("/api/v1/profiles/{profileId}/char-selection-options", PROFILE_ID))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
}
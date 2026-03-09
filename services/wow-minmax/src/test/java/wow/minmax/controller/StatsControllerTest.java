package wow.minmax.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

	@Test
	void getAbilityStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/{playerId}/ability", PLAYER_ID))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getCharacterStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/{playerId}/character", PLAYER_ID))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getSpecialAbilityStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/{playerId}/special", PLAYER_ID))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getRotationStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/{playerId}/rotation", PLAYER_ID))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getTalentStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/{playerId}/talent", PLAYER_ID))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}
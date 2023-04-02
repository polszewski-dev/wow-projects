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
	void getSpellStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/{characterId}/spell", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getCharacterStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/{characterId}/character", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getSpecialAbilityStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/{characterId}/special", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}
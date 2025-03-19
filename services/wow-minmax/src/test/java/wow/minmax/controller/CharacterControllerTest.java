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
 * Date: 2023-04-04
 */
@WebMvcTest(CharacterController.class)
class CharacterControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	void testGetCharacter() throws Exception {
		mockMvc.perform(get("/api/v1/characters/{characterId}", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}
package wow.minmax.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.minmax.client.dto.ProfessionDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wow.commons.model.profession.ProfessionId.ALCHEMY;
import static wow.commons.model.profession.ProfessionSpecializationId.TRANSMUTATION_MASTER;

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

	@Test
	void getAvailableProfessions() throws Exception {
		mockMvc.perform(get("/api/v1/characters/{characterId}/professions", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void changeProfession() throws Exception {
		var professionDTO = new ProfessionDTO(
			ALCHEMY.name(),
			null,
			null,
			null,
			TRANSMUTATION_MASTER.name()
		);

		var objectMapper = new ObjectMapper();
		var requestBody = objectMapper.writeValueAsString(professionDTO);

		mockMvc.perform(
				put("/api/v1/characters/{characterId}/professions/{index}", CHARACTER_KEY, 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}
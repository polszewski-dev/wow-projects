package wow.minmax.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.commons.client.dto.ConsumableDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: POlszewski
 * Date: 2025-03-19
 */
@WebMvcTest(ConsumableController.class)
class ConsumableControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	void getConsumables() throws Exception {
		mockMvc.perform(get("/api/v1/consumables/{characterId}", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void enableConsumable() throws Exception {
		var consumableDTO = new ConsumableDTO(
				22839, "Destruction Potion", null, null, null, true
		);

		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(consumableDTO);

		mockMvc.perform(put("/api/v1/consumables/{characterId}", CHARACTER_KEY)
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}
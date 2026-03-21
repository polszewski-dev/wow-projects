package wow.minmax.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.minmax.client.dto.BuffDTO;
import wow.minmax.client.dto.OptionStatusDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wow.test.commons.BuffNames.FEL_ARMOR;

/**
 * User: POlszewski
 * Date: 2023-01-02
 */
@WebMvcTest(BuffController.class)
class BuffControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	void getBuffs() throws Exception {
		mockMvc.perform(get("/api/v1/buffs/{playerId}", PLAYER_ID))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void enableBuff() throws Exception {
		var buffDTO = new BuffDTO(
				28189, FEL_ARMOR, 2, null, null, null
		);
		var buffStatusDTO = new OptionStatusDTO<>(
				buffDTO,
				true
		);

		var objectMapper = new ObjectMapper();
		var requestBody = objectMapper.writeValueAsString(buffStatusDTO);

		mockMvc.perform(put("/api/v1/buffs/{playerId}", PLAYER_ID)
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
				)
				.andExpect(status().isOk())
		;
	}
}
package wow.simulator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.client.dto.RngType;
import wow.simulator.client.dto.SimulationRequestDTO;
import wow.simulator.converter.PlayerConverter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: POlszewski
 * Date: 2025-03-23
 */
@WebMvcTest(SimulationController.class)
class SimulationControllerTest extends WowSimulatorSpringTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	PlayerConverter playerConverter;

	@Test
	void simulate() throws Exception {
		var playerDTO = playerConverter.convert(player);
		var duration = 60;
		var rngType = RngType.REAL;
		var request = new SimulationRequestDTO(playerDTO, duration, rngType);

		var objectMapper = new ObjectMapper();
		var requestBody = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/v1/simulations")
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@BeforeEach
	public void setUp() {
		setupTestObjects();
		getCharacterService().applyDefaultCharacterTemplate(player);
	}
}
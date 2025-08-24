package wow.estimator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.estimator.client.dto.stats.*;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2025-03-19
 */
@WebMvcTest(StatsController.class)
class StatsControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	void getSpellStats() throws Exception {
		var playerDTO = playerConverter.convert(character);
		var request = new GetSpellStatsRequestDTO(playerDTO, List.of(SHADOW_BOLT), true, 10);

		var objectMapper = new ObjectMapper();
		var requestBody = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/v1/stats/spell")
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getCharacterStats() throws Exception {
		var playerDTO = playerConverter.convert(character);
		var request = new GetCharacterStatsRequestDTO(playerDTO, true);

		var objectMapper = new ObjectMapper();
		var requestBody = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/v1/stats/character")
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getSpecialAbilityStats() throws Exception {
		var playerDTO = playerConverter.convert(character);
		var request = new GetSpecialAbilityStatsRequestDTO(playerDTO);

		var objectMapper = new ObjectMapper();
		var requestBody = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/v1/stats/special")
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getRotationStats() throws Exception {
		var playerDTO = playerConverter.convert(character);
		var request = new GetRotationStatsRequestDTO(playerDTO);

		var objectMapper = new ObjectMapper();
		var requestBody = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/v1/stats/rotation")
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getTalentStats() throws Exception {
		var playerDTO = playerConverter.convert(character);
		var request = new GetTalentStatsRequestDTO(playerDTO);

		var objectMapper = new ObjectMapper();
		var requestBody = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/v1/stats/talent")
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}
package wow.minmax.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.commons.model.categorization.ItemSlot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: POlszewski
 * Date: 2024-04-17
 */
@WebMvcTest(EquipmentOptionsController.class)
public class EquipmentOptionsControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	void getEquipmentOptions() throws Exception {
		mockMvc.perform(get("/api/v1/equipment-options/{characterId}", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getItemOptions() throws Exception {
		mockMvc.perform(get("/api/v1/equipment-options/{characterId}/item/{slot}", CHARACTER_KEY, ItemSlot.CHEST))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getEnchantOptions() throws Exception {
		mockMvc.perform(get("/api/v1/equipment-options/{characterId}/enchant", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getGemOptions() throws Exception {
		mockMvc.perform(get("/api/v1/equipment-options/{characterId}/gem", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}

package wow.minmax.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.evaluator.client.converter.upgrade.ItemFilterConverter;
import wow.evaluator.client.dto.upgrade.ItemFilterDTO;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
@WebMvcTest(UpgradeController.class)
class UpgradeControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ItemFilterConverter itemFilterConverter;

	@Test
	void findUpgrades() throws Exception {
		ItemFilterDTO convert = itemFilterConverter.convert(ItemFilter.everything());

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		String requestBody = objectMapper.writeValueAsString(convert);

		mockMvc.perform(
						get("/api/v1/upgrades/{characterId}/slot/{slotGroup}", CHARACTER_KEY, ItemSlotGroup.CHEST)
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerCharacterService).getPlayer(CHARACTER_KEY);
	}
}
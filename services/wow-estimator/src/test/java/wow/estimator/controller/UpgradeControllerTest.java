package wow.estimator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.estimator.client.dto.upgrade.*;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: POlszewski
 * Date: 2025-03-19
 */
@WebMvcTest(UpgradeController.class)
class UpgradeControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	void findUpgrades() throws Exception {
		var playerDTO = playerConverter.convert(character);
		var slotGroup = ItemSlotGroup.HEAD;
		var itemFilterDTO = new ItemFilterDTO(true, true, true, true, true, true);
		var itemLevelFilterDTO = new ItemLevelFilterDTO(Map.of());
		var gemFilterDTO = new GemFilterDTO(true);
		var enchantNames = Set.<String>of();
		var maxUpgrades = 10;
		var request = new FindUpgradesRequestDTO(playerDTO, slotGroup, itemFilterDTO, itemLevelFilterDTO, gemFilterDTO, enchantNames, maxUpgrades);

		var objectMapper = new ObjectMapper();
		var requestBody = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/v1/upgrades")
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getBestItemVariant() throws Exception {
		var playerDTO = playerConverter.convert(character);
		var itemSlot = ItemSlot.HEAD;
		var item = character.getEquippedItem(itemSlot).getItem();
		int itemId = item.getId().value();
		var gemFilterDTO = new GemFilterDTO(true);
		var enchantNames = Set.<String>of();
		var request = new GetBestItemVariantRequestDTO(playerDTO, itemId, itemSlot, gemFilterDTO, enchantNames);

		var objectMapper = new ObjectMapper();
		var requestBody = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/v1/upgrades/best-variant")
								.contentType(MediaType.APPLICATION_JSON)
								.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}
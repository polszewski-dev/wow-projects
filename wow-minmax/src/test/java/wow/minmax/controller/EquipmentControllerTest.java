package wow.minmax.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.converter.dto.EquippableItemConverter;
import wow.minmax.model.dto.EquippableItemDTO;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: POlszewski
 * Date: 2023-01-02
 */
@WebMvcTest(EquipmentController.class)
class EquipmentControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	EquippableItemConverter equippableItemConverter;

	@Test
	void getEquipmentTest() throws Exception {
		mockMvc.perform(get("/api/v1/equipment/{characterId}", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getEquipmentOptions() throws Exception {
		mockMvc.perform(get("/api/v1/equipment/{characterId}/options", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void changeItemBestVariant() throws Exception {
		ItemSlot slot = ItemSlot.CHEST;
		EquippableItem chest = getItem("Sunfire Robe");

		mockMvc.perform(get("/api/v1/equipment/{characterId}/change/item/{slot}/{itemId}/best/variant", CHARACTER_KEY, slot, chest.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).changeItemBestVariant(CHARACTER_KEY, ItemSlot.CHEST, chest.getId());
	}

	@Test
	void changeItem() throws Exception {
		EquippableItem chest = character.getEquipment().getChest();
		EquippableItemDTO chestDTO = equippableItemConverter.convert(chest);

		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(chestDTO);

		mockMvc.perform(
				put("/api/v1/equipment/{characterId}/change/item/{slot}", CHARACTER_KEY, ItemSlot.CHEST)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).changeItem(eq(CHARACTER_KEY), eq(ItemSlot.CHEST), any());
	}

	@Test
	void changeItemGroup() throws Exception {
		EquippableItem chest = character.getEquipment().getChest();
		EquippableItemDTO chestDTO = equippableItemConverter.convert(chest);

		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(List.of(chestDTO));

		mockMvc.perform(
				put("/api/v1/equipment/{characterId}/change/item/group/{slotGroup}", CHARACTER_KEY, ItemSlotGroup.CHEST)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
				)
				.andExpect(status().isOk())
		;

		verify(playerProfileService).changeItemGroup(eq(CHARACTER_KEY), eq(ItemSlotGroup.CHEST), any());
	}

	@Test
	void resetEquipment() throws Exception {
		mockMvc.perform(get("/api/v1/equipment/{characterId}/reset", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).resetEquipment(CHARACTER_KEY);
	}

	@Test
	void getEquipmentSocketStatus() throws Exception {
		mockMvc.perform(get("/api/v1/equipment/{characterId}/socket/status", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}
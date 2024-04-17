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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
		mockMvc.perform(get("/api/v1/equipments/{characterId}", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void equipItemBestVariant() throws Exception {
		EquippableItem chest = getItem("Sunfire Robe");
		EquippableItemDTO chestDTO = equippableItemConverter.convert(chest);

		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(chestDTO);

		mockMvc.perform(
				put("/api/v1/equipments/{characterId}/slot/{slot}?best-variant=true", CHARACTER_KEY, ItemSlot.CHEST)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).equipItem(eq(CHARACTER_KEY), eq(ItemSlot.CHEST), any(), eq(true));
	}

	@Test
	void equipItem() throws Exception {
		EquippableItem chest = character.getEquipment().getChest();
		EquippableItemDTO chestDTO = equippableItemConverter.convert(chest);

		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(chestDTO);

		mockMvc.perform(
				put("/api/v1/equipments/{characterId}/slot/{slot}", CHARACTER_KEY, ItemSlot.CHEST)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).equipItem(eq(CHARACTER_KEY), eq(ItemSlot.CHEST), any(), eq(false));
	}

	@Test
	void equipItemGroup() throws Exception {
		EquippableItem chest = character.getEquipment().getChest();
		EquippableItemDTO chestDTO = equippableItemConverter.convert(chest);

		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(List.of(chestDTO));

		mockMvc.perform(
				put("/api/v1/equipments/{characterId}/slot-group/{slotGroup}", CHARACTER_KEY, ItemSlotGroup.CHEST)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody)
				)
				.andExpect(status().isOk())
		;

		verify(playerProfileService).equipItemGroup(eq(CHARACTER_KEY), eq(ItemSlotGroup.CHEST), any());
	}

	@Test
	void resetEquipment() throws Exception {
		mockMvc.perform(delete("/api/v1/equipments/{characterId}", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).resetEquipment(CHARACTER_KEY);
	}

	@Test
	void getEquipmentSocketStatus() throws Exception {
		mockMvc.perform(get("/api/v1/equipments/{characterId}/socket-status", CHARACTER_KEY))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}
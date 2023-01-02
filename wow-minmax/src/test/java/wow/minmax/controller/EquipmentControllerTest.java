package wow.minmax.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

	@Test
	void getEquipmentTest() throws Exception {
		mockMvc.perform(get("/api/v1/equipment/{profileId}", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getEquipmentOptions() throws Exception {
		mockMvc.perform(get("/api/v1/equipment/{profileId}/options", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void changeItem() throws Exception {
		ItemSlot slot = ItemSlot.CHEST;
		EquippableItem chest = getItem("Sunfire Robe");

		mockMvc.perform(get("/api/v1/equipment/{profileId}/change/item/{slot}/{itemId}", profile.getProfileId(), slot, chest.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).changeItem(profile.getProfileId(), ItemSlot.CHEST, chest.getId());
	}

	@Test
	void changeEnchant() throws Exception {
		Enchant enchant = getEnchant("Enchant Chest - Exceptional Stats");

		mockMvc.perform(get("/api/v1/equipment/{profileId}/change/enchant/{slot}/{enchantId}", profile.getProfileId(), ItemSlot.CHEST, enchant.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).changeEnchant(profile.getProfileId(), ItemSlot.CHEST, enchant.getId());
	}

	@Test
	void changeGem() throws Exception {
		Gem violetGem = getGem("Glowing Shadowsong Amethyst");

		mockMvc.perform(get("/api/v1/equipment/{profileId}/change/gem/{slot}/{socketNo}/{gemId}", profile.getProfileId(), ItemSlot.CHEST, 1, violetGem.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).changeGem(profile.getProfileId(), ItemSlot.CHEST, 1, violetGem.getId());
	}

	@Test
	void resetEquipment() throws Exception {
		mockMvc.perform(get("/api/v1/equipment/{profileId}/reset", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).resetEquipment(profile.getProfileId());
	}

	@Test
	void getEquipmentSocketStatus() throws Exception {
		mockMvc.perform(get("/api/v1/equipment/{profileId}/socket/status", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}
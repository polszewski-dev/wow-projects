package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.pve.Phase;
import wow.minmax.service.ItemService;
import wow.minmax.service.PlayerProfileService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * User: POlszewski
 * Date: 2022-10-19
 */
@WebMvcTest(PlayerProfileController.class)
class PlayerProfileControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	PlayerProfileService playerProfileService;

	@MockBean
	@Qualifier("itemService")
	ItemService itemService;

	@Test
	void getPlayerProfileList() throws Exception {
		mockMvc.perform(get("/api/v1/profile/list"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].profileId", is(profile.getProfileId().toString())))
				.andExpect(jsonPath("$[0].profileName", is(profile.getProfileName())))
		;

		verify(playerProfileService).getPlayerProfileList();
	}

	@Test
	void getPlayerProfile() throws Exception {
		mockMvc.perform(get("/api/v1/profile/{profileId}", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.profileId", is(profile.getProfileId().toString())))
				.andExpect(jsonPath("$.profileName", is(profile.getProfileName())))
		;

		verify(playerProfileService).getPlayerProfile(profile.getProfileId());
	}

	@Test
	void getPlayerProfileWithOptions() throws Exception {
		mockMvc.perform(get("/api/v1/profile/{profileId}?addOptions=true", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.profileId", is(profile.getProfileId().toString())))
				.andExpect(jsonPath("$.profileName", is(profile.getProfileName())))
		;

		verify(playerProfileService).getPlayerProfile(profile.getProfileId());
	}


	@Test
	void createPlayerProfile() throws Exception {
		String profileName = "testProfile";
		Phase phase = Phase.TBC_P5;

		mockMvc.perform(get("/api/v1/profile/create/name/{profileName}/phase/{phase}", profileName, phase))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).createPlayerProfile(profileName, phase);
	}

	@Test
	void copyPlayerProfile() throws Exception {
		String profileName = "testProfileCopy";
		Phase phase = Phase.TBC_P5;

		mockMvc.perform(get("/api/v1/profile/copy/{copiedProfileId}/name/{profileName}/phase/{phase}", profile.getProfileId(), profileName, phase))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).copyPlayerProfile(profile.getProfileId(), profileName, phase);
	}

	@Test
	void changeItem() throws Exception {
		ItemSlot slot = ItemSlot.CHEST;

		mockMvc.perform(get("/api/v1/profile/{profileId}/change/item/{slot}/{itemId}", profile.getProfileId(), slot, chest.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).changeItem(profile.getProfileId(), ItemSlot.CHEST, chest.getId());
	}

	@Test
	void changeEnchant() throws Exception {
		mockMvc.perform(get("/api/v1/profile/{profileId}/change/enchant/{slot}/{enchantId}", profile.getProfileId(), ItemSlot.CHEST, enchant.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).changeEnchant(profile.getProfileId(), ItemSlot.CHEST, enchant.getId());
	}

	@Test
	void changeGem() throws Exception {
		mockMvc.perform(get("/api/v1/profile/{profileId}/change/gem/{slot}/{socketNo}/{gemId}", profile.getProfileId(), ItemSlot.CHEST, 1, blueGem.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).changeGem(profile.getProfileId(), ItemSlot.CHEST, 1, blueGem.getId());
	}

	@Test
	void enableBuff() throws Exception {
		mockMvc.perform(get("/api/v1/profile/{profileId}/enable/buff/{buffId}/{enabled}", profile.getProfileId(), buff.getId(), true))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void resetProfile() throws Exception {
		mockMvc.perform(get("/api/v1/profile/{profileId}/reset/equipment", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).resetEquipment(profile.getProfileId());
	}

	@BeforeEach
	void setup() {
		createMockObjects();

		when(playerProfileService.getPlayerProfileList()).thenReturn(List.of(profile));
		when(playerProfileService.getPlayerProfile(profile.getProfileId())).thenReturn(profile);
		when(playerProfileService.createPlayerProfile(any(), any())).thenReturn(profile);
		when(playerProfileService.copyPlayerProfile(any(), any(), any())).thenReturn(profile);
		when(playerProfileService.resetEquipment(any())).thenReturn(profile);
		when(playerProfileService.changeItem(any(), any(), anyInt())).thenReturn(profile);
		when(playerProfileService.changeEnchant(any(), any(), anyInt())).thenReturn(profile);
		when(playerProfileService.changeGem(any(), any(), anyInt(), anyInt())).thenReturn(profile);
		when(playerProfileService.enableBuff(any(), anyInt(), anyBoolean())).thenReturn(profile);

		when(itemService.getItem(chest.getId())).thenReturn(chest);
		when(itemService.getItem(trinket.getId())).thenReturn(trinket);
		when(itemService.getItemsBySlot(any(), eq(ItemSlot.CHEST))).thenReturn(List.of(chest));
		when(itemService.getItemsBySlot(any(), eq(ItemSlot.TRINKET_1))).thenReturn(List.of(trinket));
		when(itemService.getItemsBySlot(any(), eq(ItemSlot.TRINKET_2))).thenReturn(List.of(trinket));

		when(itemService.getEnchants(any(), eq(ItemType.CHEST))).thenReturn(List.of(enchant));

		when(itemService.getGems(any(), any(), anyInt(), anyBoolean())).thenReturn(List.of(redGem, yellowGem, blueGem));
		when(itemService.getGems(any(), any(), anyBoolean())).thenReturn(List.of(redGem, yellowGem, blueGem));
	}
}
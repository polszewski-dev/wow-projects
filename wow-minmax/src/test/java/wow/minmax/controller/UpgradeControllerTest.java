package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.commons.model.Percent;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.UpgradeService;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
@WebMvcTest(UpgradeController.class)
class UpgradeControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	UpgradeService upgradeService;

	@MockBean
	PlayerProfileService playerProfileService;

	@Test
	void findUpgrades() throws Exception {
		mockMvc.perform(get("/api/v1/upgrade/{profileId}/slot/{slotGroup}", profile.getProfileId(), ItemSlotGroup.CHEST))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(1)))
		;

		verify(playerProfileService).getPlayerProfile(profile.getProfileId());
		verify(upgradeService).findUpgrades(refEq(profile), eq(ItemSlotGroup.CHEST), eq(profile.getDamagingSpell()));
	}

	@Test
	void findAllUpgrades() throws Exception {
		mockMvc.perform(get("/api/v1/upgrade/{profileId}", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;

		verify(playerProfileService).getPlayerProfile(profile.getProfileId());
		for (ItemSlotGroup slotGroup : ItemSlotGroup.values()) {
			if (!IGNORED_SLOT_GROUPS.contains(slotGroup)) {
				verify(upgradeService).findUpgrades(refEq(profile), eq(slotGroup), eq(profile.getDamagingSpell()));
			}
		}
	}

	private static final Set<ItemSlotGroup> IGNORED_SLOT_GROUPS = Set.of(
			ItemSlotGroup.TABARD,
			ItemSlotGroup.SHIRT,
			ItemSlotGroup.FINGER_1,
			ItemSlotGroup.FINGER_2,
			ItemSlotGroup.TRINKET_1,
			ItemSlotGroup.TRINKET_2,
			ItemSlotGroup.MAIN_HAND,
			ItemSlotGroup.OFF_HAND
	);

	@BeforeEach
	void setup() {
		createMockObjects();

		PlayerProfile profileCopy = profile.copy();
		profileCopy.getEquipment().getChest().gem(redGem, redGem, redGem);

		when(playerProfileService.getPlayerProfile(profile.getProfileId())).thenReturn(profile);
		when(upgradeService.findUpgrades(refEq(profile), any(), eq(profile.getDamagingSpell()))).thenReturn(List.of());
		when(upgradeService.findUpgrades(refEq(profile), eq(ItemSlotGroup.CHEST), eq(profile.getDamagingSpell())))
				.thenReturn(List.of(new Comparison(profileCopy.getEquipment(), profile.getEquipment(), Percent.of(1))));
	}
}
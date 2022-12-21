package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.character.model.snapshot.CritMode;
import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SpellStatistics;
import wow.commons.model.attributes.Attributes;
import wow.minmax.model.PlayerSpellStats;
import wow.minmax.service.CalculationService;
import wow.minmax.service.PlayerProfileService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
@WebMvcTest(StatsController.class)
class StatsControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	PlayerProfileService playerProfileService;

	@MockBean
	CalculationService calculationService;

	@Test
	void getSpellStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/spell/{profileId}", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getPlayerStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/player/{profileId}", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void getSpecialAbilityStats() throws Exception {
		mockMvc.perform(get("/api/v1/stats/special/{profileId}", profile.getProfileId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@BeforeEach
	void setup() {
		createMockObjects();

		when(playerProfileService.getPlayerProfile(profile.getProfileId())).thenReturn(profile);

		Snapshot snapshot = new Snapshot(profile.getDamagingSpell(), profile.getCharacterInfo(), profile.getEnemyInfo(), profile.getStats());
		SpellStatistics statistics = snapshot.getSpellStatistics(CritMode.AVERAGE, false);

		when(calculationService.getSnapshot(any(), any(), any())).thenReturn(snapshot);
		when(calculationService.getAbilityEquivalent(any(), any(), any(), any())).thenReturn(Attributes.EMPTY);
		when(calculationService.getPlayerSpellStats(any(), any())).thenReturn(new PlayerSpellStats(profile, statistics, 1, 2, 3));
		when(calculationService.getSpellStatistics(any(), any())).thenReturn(statistics);
		when(calculationService.getSpellStatistics(any(), any(), any())).thenReturn(statistics);
	}
}
package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.character.BaseStatInfo;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CombatRatingInfo;
import wow.commons.model.character.Race;
import wow.commons.model.spells.Snapshot;
import wow.commons.model.spells.SpellStatistics;
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

		BaseStatInfo baseStats = new BaseStatInfo(70, CharacterClass.WARLOCK, Race.ORC, 0, 0, 0, 0, 0, 0, 0, Percent.ZERO, 100);
		CombatRatingInfo cr = new CombatRatingInfo(70, 10, 10, 10);
		Snapshot snapshot = new Snapshot(profile.getDamagingSpell(), baseStats, cr, profile.getStats(), null, profile.getEnemyType());
		SpellStatistics statistics = snapshot.getSpellStatistics(Snapshot.CritMode.AVERAGE, false);
		statistics.setSnapshot(snapshot);

		when(calculationService.getSnapshot(any(), any(), any())).thenReturn(snapshot);
		when(calculationService.getAbilityEquivalent(any(), any(), any(), any())).thenReturn(Attributes.EMPTY);
		when(calculationService.getPlayerSpellStats(any(), any())).thenReturn(new PlayerSpellStats(profile, statistics, 1, 2, 3));
		when(calculationService.getSpellStatistics(any(), any())).thenReturn(statistics);
		when(calculationService.getSpellStatistics(any(), any(), any())).thenReturn(statistics);
	}
}
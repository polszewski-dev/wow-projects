package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.SUMMON_FELGUARD;
import static wow.test.commons.TalentNames.MASTER_SUMMONER;

/**
 * User: POlszewski
 * Date: 2026-08-31
 */
class MasterSummonerTest extends TbcWarlockTalentSimulationTest {
	/*
	Reduces the casting time of your Imp, Voidwalker, Succubus, Incubus, Felhunter and Fel Guard Summoning spells by 4 sec and the Mana cost by 40%.
	 */
	@ParameterizedTest
	@CsvSource({
			"1, Summon Imp",
			"1, Summon Voidwalker",
			"1, Summon Succubus",
			"1, Summon Felhunter",
			"1, Summon Felguard",
			"2, Summon Imp",
			"2, Summon Voidwalker",
			"2, Summon Succubus",
			"2, Summon Felhunter",
			"2, Summon Felguard",
	})
	void cast_time_is_reduced(int rank, String abilityName) {
		enableSummonFelguardTalent(abilityName);

		simulateTalent(MASTER_SUMMONER, rank, abilityName);

		assertCastTimeIsReducedBy(2 * rank);
	}

	@ParameterizedTest
	@CsvSource({
			"1, Summon Imp",
			"1, Summon Voidwalker",
			"1, Summon Succubus",
			"1, Summon Felhunter",
			"1, Summon Felguard",
			"2, Summon Imp",
			"2, Summon Voidwalker",
			"2, Summon Succubus",
			"2, Summon Felhunter",
			"2, Summon Felguard",
	})
	void mana_cost_is_reduced(int rank, String abilityName) {
		enableSummonFelguardTalent(abilityName);

		simulateTalent(MASTER_SUMMONER, rank, abilityName);

		assertManaCostIsReducedByPct(20 * rank);
	}

	private void enableSummonFelguardTalent(String abilityName) {
		if (abilityName.equals(SUMMON_FELGUARD)) {
			enableTalent(player, SUMMON_FELGUARD, 1);
			enableTalent(player2, SUMMON_FELGUARD, 1);
		}
	}
}

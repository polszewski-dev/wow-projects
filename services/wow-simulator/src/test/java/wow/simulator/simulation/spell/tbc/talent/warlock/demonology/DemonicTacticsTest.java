package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.SUMMON_IMP;
import static wow.test.commons.TalentNames.DEMONIC_TACTICS;

/**
 * User: POlszewski
 * Date: 2026-08-27
 */
class DemonicTacticsTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases melee and spell critical strike chance for you and your summoned demon by 5%.
	 */
	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void players_crit_pct_is_increased(int rank) {
		enableTalent(player, DEMONIC_TACTICS, rank);

		assertSpellCritPctIsIncreasedBy(player, rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void pets_crit_pct_is_increased(int rank) {
		enableTalent(player, DEMONIC_TACTICS, rank);

		player.cast(SUMMON_IMP);

		updateUntil(30);

		assertSpellCritPctIsIncreasedBy(pet, rank);
	}
}

package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.simulator.simulation.spell.tbc.TbcSpellInfos.FIREBOLT_INFO;
import static wow.test.commons.AbilityNames.FIREBOLT;
import static wow.test.commons.AbilityNames.SUMMON_IMP;
import static wow.test.commons.TalentNames.IMPROVED_FIREBOLT;

/**
 * User: POlszewski
 * Date: 2026-08-27
 */
class ImprovedFireboltTest extends TbcWarlockTalentSimulationTest {
	/*
	Reduces the casting time of your Imp's Firebolt spell by 0.5 sec.
	 */
	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void firebolt_cast_time_is_reduced(int rank) {
		enableTalent(IMPROVED_FIREBOLT, rank);

		player.cast(SUMMON_IMP);
		summonedPetCasts(player, FIREBOLT);

		updateUntil(30);

		assertCastTimeIsReducedBy(FIREBOLT_INFO, pet, 0.25 * rank);
	}
}

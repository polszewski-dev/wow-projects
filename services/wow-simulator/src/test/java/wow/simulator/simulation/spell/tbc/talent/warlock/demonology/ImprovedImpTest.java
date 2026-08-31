package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.simulator.simulation.spell.tbc.TbcSpellInfos.FIREBOLT_INFO;
import static wow.test.commons.AbilityNames.FIREBOLT;
import static wow.test.commons.AbilityNames.SUMMON_IMP;
import static wow.test.commons.TalentNames.IMPROVED_IMP;

/**
 * User: POlszewski
 * Date: 2026-08-27
 */
class ImprovedImpTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the effect of your Imp's Firebolt, Fire Shield, and Blood Pact spells by 30%.
	 */
	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void firebolt_damage_is_increased(int rank) {
		enableTalent(IMPROVED_IMP, rank);

		player.cast(SUMMON_IMP);
		summonedPetCasts(player, FIREBOLT);

		updateUntil(30);

		assertDamageDone(FIREBOLT_INFO, target, pet, 0, 10 * rank);
	}
}

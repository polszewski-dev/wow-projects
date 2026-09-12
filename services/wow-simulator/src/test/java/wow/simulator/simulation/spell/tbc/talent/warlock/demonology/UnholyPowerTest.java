package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.simulator.simulation.spell.tbc.TbcSpellInfos.FIREBOLT_INFO;
import static wow.test.commons.AbilityNames.FIREBOLT;
import static wow.test.commons.AbilityNames.SUMMON_IMP;
import static wow.test.commons.TalentNames.UNHOLY_POWER;

/**
 * User: POlszewski
 * Date: 2026-08-27
 */
class UnholyPowerTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the damage done by your Voidwalker, Succubus, Incubus, Felhunter and Felguard's melee attacks and your Imp's Firebolt by 20%.
	 */
	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void pets_damage_is_increased(int rank) {
		enableTalent(UNHOLY_POWER, rank);

		player.cast(SUMMON_IMP);
		player.petCast(FIREBOLT);

		updateUntil(30);

		assertDamageDone(FIREBOLT_INFO, target, pet, 0, 4 * rank);
	}
}

package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.simulator.simulation.spell.tbc.TbcSpellInfos.LASH_OF_PAIN_INFO;
import static wow.test.commons.AbilityNames.LASH_OF_PAIN;
import static wow.test.commons.AbilityNames.SUMMON_SUCCUBUS;
import static wow.test.commons.TalentNames.IMPROVED_LASH_OF_PAIN;

/**
 * User: POlszewski
 * Date: 2026-08-27
 */
class ImprovedLashOfPainTest extends TbcWarlockTalentSimulationTest {
	/*
	Reduces the cooldown of your Succubus' and Incubus' Lash of Pain spell by 6 sec.

	 */
	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void lash_of_pain_cooldown_is_reduced(int rank) {
		enableTalent(IMPROVED_LASH_OF_PAIN, rank);

		player.cast(SUMMON_SUCCUBUS);
		player.petCast(LASH_OF_PAIN);

		updateUntil(30);

		assertCooldownIsReducedBy(LASH_OF_PAIN_INFO, pet, 3 * rank);
	}
}

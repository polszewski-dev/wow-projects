package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.simulator.simulation.spell.tbc.TbcSpellInfos.LASH_OF_PAIN_INFO;
import static wow.test.commons.AbilityNames.LASH_OF_PAIN;
import static wow.test.commons.AbilityNames.SUMMON_SUCCUBUS;
import static wow.test.commons.TalentNames.IMPROVED_SUCCUBUS;

/**
 * User: POlszewski
 * Date: 2026-08-27
 */
class ImprovedSuccubusTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the effect of your Succubus' and Incubus' Lash of Pain and Soothing Kiss spells by 30%, and increases the duration of your Succubus' and Incubus' Seduction and Lesser Invisibility spells by 30%.
	 */
	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void firebolt_damage_is_increased(int rank) {
		enableTalent(IMPROVED_SUCCUBUS, rank);

		player.cast(SUMMON_SUCCUBUS);
		summonedPetCasts(player, LASH_OF_PAIN);

		updateUntil(30);

		assertDamageDone(LASH_OF_PAIN_INFO, target, pet, 0, 10 * rank);
	}
}

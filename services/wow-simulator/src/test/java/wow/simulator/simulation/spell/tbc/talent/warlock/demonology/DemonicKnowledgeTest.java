package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.SUMMON_IMP;
import static wow.test.commons.TalentNames.DEMONIC_KNOWLEDGE;

/**
 * User: POlszewski
 * Date: 2026-08-27
 */
class DemonicKnowledgeTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases your spell damage by an amount equal to 12% of the total of your active demon's Stamina plus Intellect.
	 */
	@Disabled("until pet/master conversions are implemented")
	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void spell_damage_is_increased(int rank) {
		enableTalent(player, DEMONIC_KNOWLEDGE, rank);

		player.cast(SUMMON_IMP);

		updateUntil(30);

		assertSpellDamageIsIncreasedBy(player, 100);
	}
}

package wow.simulator.simulation.spell.tbc.talent.shaman.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.FROST_SHOCK;
import static wow.test.commons.TalentNames.NATURES_GUIDANCE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class NaturesGuidanceTest extends TbcShamanTalentSimulationTest {
	/*
	Increases your chance to hit with melee attacks and spells by 3%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void spell_hit_chance_is_increased(int rank) {
		simulateTalent(NATURES_GUIDANCE, rank, FROST_SHOCK);

		assertHitChanceIsIncreasedByPct(rank);
	}
}

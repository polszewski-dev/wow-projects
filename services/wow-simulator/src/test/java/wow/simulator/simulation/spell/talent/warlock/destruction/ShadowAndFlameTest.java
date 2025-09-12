package wow.simulator.simulation.spell.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.talent.TalentId.SHADOW_AND_FLAME;
import static wow.test.commons.BuffNames.FEL_ARMOR;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ShadowAndFlameTest extends WarlockSpellSimulationTest {
	/*
	Your Shadow Bolt and Incinerate spells gain an additional 20% of your bonus spell damage effects.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void additionalSDBonus(int rank) {
		enableBuff(FEL_ARMOR, 2);

		enableTalent(SHADOW_AND_FLAME, rank);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertDamageDone(SHADOW_BOLT, SHADOW_BOLT_INFO.damage(4 * rank, 100));
	}
}

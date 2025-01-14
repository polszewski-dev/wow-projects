package wow.simulator.simulation.spell.talent.warlock.destruction;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.talent.TalentId.SOUL_LEECH;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SoulLeechTest extends WarlockSpellSimulationTest {
	/*
	Gives your Shadow Bolt, Shadowburn, Soul Fire, Incinerate, Searing Pain and Conflagrate spells a 30% chance to return health equal to 20% of the damage caused.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void procChanceIsCorrect(int rank) {
		enableTalent(SOUL_LEECH, rank);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertLastEventChance(10 * rank);
	}

	@Disabled
	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void procDamageIsCorrect(int rank) {
		enableTalent(SOUL_LEECH, rank);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertHealthGained(SHADOW_BOLT, player, (int) (575 * 0.2));
	}
}

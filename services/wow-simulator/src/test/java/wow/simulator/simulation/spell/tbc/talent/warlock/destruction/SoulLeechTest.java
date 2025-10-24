package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.simulator.util.CalcUtils.getPercentOf;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.TalentNames.SOUL_LEECH;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SoulLeechTest extends TbcWarlockSpellSimulationTest {
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

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void procHealIsCorrect(int rank) {
		eventsOnlyOnFollowingRolls(0);

		enableTalent(SOUL_LEECH, rank);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertHealthGained(SOUL_LEECH, player, getPercentOf(20, SHADOW_BOLT_INFO.damage()));
	}

	@Override
	protected void afterSetUp() {
		setHealth(player, 1000);
	}
}

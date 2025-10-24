package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.LIFE_TAP;
import static wow.test.commons.TalentNames.IMPROVED_LIFE_TAP;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedLifeTapTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases the amount of Mana awarded by your Life Tap spell by 20%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void improvedLifeTap(int rank) {
		enableTalent(IMPROVED_LIFE_TAP, rank);
		setMana(player, 0);

		player.cast(LIFE_TAP);

		updateUntil(30);

		assertIsIncreasedByPct(player.getCurrentMana() - regeneratedMana, (int) LIFE_TAP_INFO.damage(), 10 * rank);

		assertDamageDone(LIFE_TAP, player, LIFE_TAP_INFO.damage(), 10 * rank);
		assertManaGained(LIFE_TAP, player, LIFE_TAP_INFO.damage(), 10 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void improvedLifeTapAndSp(int rank) {
		addSpBonus(40);

		enableTalent(IMPROVED_LIFE_TAP, rank);
		setMana(player, 0);

		player.cast(LIFE_TAP);

		updateUntil(30);

		assertIsIncreasedByPct(player.getCurrentMana() - regeneratedMana, (int) LIFE_TAP_INFO.damage(40), 10 * rank);

		assertDamageDone(LIFE_TAP, player, LIFE_TAP_INFO.damage(40), 10 * rank);
		assertManaGained(LIFE_TAP, player, LIFE_TAP_INFO.damage(40), 10 * rank);
	}
}

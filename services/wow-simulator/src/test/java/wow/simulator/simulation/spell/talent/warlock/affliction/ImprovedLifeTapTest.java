package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.LIFE_TAP;
import static wow.commons.model.talent.TalentId.IMPROVED_LIFE_TAP;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedLifeTapTest extends WarlockSpellSimulationTest {
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

		assertIsIncreasedByPct(player.getCurrentMana(), 582, 10 * rank);

		assertDamageDone(LIFE_TAP, player, 582, 10 * rank);
		assertManaGained(LIFE_TAP, player, 582, 10 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void improvedLifeTapAndSp(int rank) {
		equip("Azuresong Mageblade");

		enableTalent(IMPROVED_LIFE_TAP, rank);
		setMana(player, 0);

		player.cast(LIFE_TAP);

		updateUntil(30);

		assertIsIncreasedByPct(player.getCurrentMana(), 582 + (int) (40 * 0.8), 10 * rank);

		assertDamageDone(LIFE_TAP, player, 582 + (int) (40 * 0.8), 10 * rank);
		assertManaGained(LIFE_TAP, player, 582 + (int) (40 * 0.8), 10 * rank);
	}
}

package wow.simulator.simulation.spell.tbc.talent.mage.arcane;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.simulator.util.EffectType.TALENT;
import static wow.test.commons.AbilityNames.SCORCH;
import static wow.test.commons.TalentNames.ARCANE_CONCENTRATION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ArcaneConcentrationTest extends TbcMageTalentSimulationTest {
	/*
	Gives you a 10% chance of entering a Clearcasting state after any damage spell hits a target.
	The Clearcasting state reduces the mana cost of your next damage spell by 100%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void effect_is_applied(int rank) {
		enableTalent(ARCANE_CONCENTRATION, rank);

		eventsOnlyOnFollowingRolls(0);

		player.cast(SCORCH);

		updateUntil(60);

		assertEvents(
			testEvent -> testEvent.isCast() || testEvent.isEffect(),
			at(0)
					.beginCast(player, SCORCH, 1.5),
			at(1.5)
					.endCast(player, SCORCH)
					.effectApplied(ARCANE_CONCENTRATION, TALENT, player, Duration.INFINITE)
		);
	}
}

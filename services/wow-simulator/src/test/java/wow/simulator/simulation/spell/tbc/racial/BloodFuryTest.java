package wow.simulator.simulation.spell.tbc.racial;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.test.commons.AbilityNames.BLOOD_FURY;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class BloodFuryTest extends TbcWarlockSpellSimulationTest {
	@Test
	void success() {
		player.cast(BLOOD_FURY);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, BLOOD_FURY)
						.endCast(player, BLOOD_FURY)
						.cooldownStarted(player, BLOOD_FURY, 120)
						.effectApplied(BLOOD_FURY, player, 15),
				at(15)
						.effectExpired(BLOOD_FURY, player),
				at(120)
						.cooldownExpired(player, BLOOD_FURY)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		player.cast(BLOOD_FURY);
		updateUntil(10);

		var dmgBefore = statsAt(0).getSpellPower();
		var dmgAfter = statsAt(10).getSpellPower();

		assertThat(dmgBefore).isZero();
		assertThat(dmgAfter).isEqualTo(143);
	}
}

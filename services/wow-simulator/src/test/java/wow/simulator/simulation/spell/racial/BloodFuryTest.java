package wow.simulator.simulation.spell.racial;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.BLOOD_FURY;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class BloodFuryTest extends WarlockSpellSimulationTest {
	@Test
	void success() {
		player.cast(BLOOD_FURY);

		simulation.updateUntil(Time.at(180));

		assertEvents(
				at(0)
						.beginCast(player, BLOOD_FURY)
						.endCast(player, BLOOD_FURY)
						.cooldownStarted(player, BLOOD_FURY)
						.effectApplied(BLOOD_FURY, player),
				at(15)
						.effectExpired(BLOOD_FURY, player),
				at(120)
						.cooldownExpired(player, BLOOD_FURY)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		var dmgBefore = player.getStats().getSpellPower();

		player.cast(BLOOD_FURY);
		simulation.updateUntil(Time.at(10));

		var dmgAfter = player.getStats().getSpellPower();

		assertThat(dmgBefore).isZero();
		assertThat(dmgAfter).isEqualTo(143);
	}
}

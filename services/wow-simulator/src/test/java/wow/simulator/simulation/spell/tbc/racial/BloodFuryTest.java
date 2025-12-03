package wow.simulator.simulation.spell.tbc.racial;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.character.RaceId.ORC;
import static wow.test.commons.AbilityNames.BLOOD_FURY;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class BloodFuryTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases your damage and healing from spells and effects by up to 143, but reduces healing effects on you by 50%. Lasts 15 sec.
	 */

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
	void sp_increased() {
		player.cast(BLOOD_FURY);

		updateUntil(10);

		assertSpellPowerIncreasedBy(143);
	}

	@Override
	protected void beforeSetUp() {
		super.beforeSetUp();
		raceId = ORC;
	}
}

package wow.simulator.simulation.spell.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.AbilityNames.IMMOLATE;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class T4P4BonusTest extends WarlockSpellSimulationTest {
	/*
	Increases the duration of your Corruption and Immolate abilities by 3 sec.
	 */

	@Test
	void corruptionDurationIsIncreased() {
		player.cast(CORRUPTION);

		updateUntil(30);

		assertEffectDuration(CORRUPTION, target, CORRUPTION_INFO.duration() + 3);
	}

	@Test
	void immolateDurationIsIncreased() {
		player.cast(IMMOLATE);

		updateUntil(30);

		assertEffectDuration(IMMOLATE, target, IMMOLATE_INFO.duration() + 3);
	}

	@Override
	protected void afterSetUp() {
		equip("Voidheart Crown");
		equip("Voidheart Gloves");
		equip("Voidheart Leggings");
		equip("Voidheart Mantle");
	}
}

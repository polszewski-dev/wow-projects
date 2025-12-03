package wow.simulator.simulation.spell.tbc.racial;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.character.RaceId.TROLL;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.BERSERKING;

/**
 * User: POlszewski
 * Date: 2025-12-03
 */
class BerserkingTest extends TbcMageSpellSimulationTest {
	/*
	Increases your casting and attack speed by 10% to 30%.
	At full health the speed increase is 10% with a greater effect up to 30% if you are badly hurt when you activate Berserking.
	Lasts 10 sec.
	 */

	@Test
	void success() {
		player.cast(BERSERKING);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, BERSERKING)
						.endCast(player, BERSERKING)
						.decreasedResource(250, MANA, player, BERSERKING)
						.cooldownStarted(player, BERSERKING, 180)
						.effectApplied(BERSERKING, player, 10),
				at(10)
						.effectExpired(BERSERKING, player),
				at(180)
						.cooldownExpired(player, BERSERKING)
		);
	}

	@ParameterizedTest
	@CsvSource({
			"100, 10",
			"71,  10",
			"69,  20",
			"41,  20",
			"39,  30",
	})
	void haste_increased(int healthPct, int expectedHastePct) {
		setHealthPct(player, healthPct);

		player.cast(BERSERKING);

		updateUntil(10);

		assertSpellHastePctIncreasedBy(expectedHastePct);
	}

	@Override
	protected void beforeSetUp() {
		super.beforeSetUp();
		raceId = TROLL;
	}
}

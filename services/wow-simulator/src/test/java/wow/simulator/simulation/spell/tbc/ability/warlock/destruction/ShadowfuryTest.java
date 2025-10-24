package wow.simulator.simulation.spell.tbc.ability.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SHADOWFURY;

/**
 * User: POlszewski
 * Date: 2025-02-15
 */
class ShadowfuryTest extends TbcWarlockSpellSimulationTest {
	/*
	Shadowfury is unleashed, causing 612 to 728 Shadow damage and stunning all enemies within 8 yds for 2 sec.
	 */

	@Test
	void success() {
		player.cast(SHADOWFURY);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOWFURY, 0.5)
						.beginGcd(player),
				at(0.5)
						.endCast(player, SHADOWFURY)
						.decreasedResource(710, MANA, player, SHADOWFURY)
						.cooldownStarted(player, SHADOWFURY, 20)
						.decreasedResource(670, HEALTH, target, SHADOWFURY)
						.decreasedResource(670, HEALTH, target1, SHADOWFURY)
						.decreasedResource(670, HEALTH, target2, SHADOWFURY)
						.decreasedResource(670, HEALTH, target3, SHADOWFURY),
				at(1.5)
						.endGcd(player),
				at(20.5)
						.cooldownExpired(player, SHADOWFURY)
		);
	}

	Unit target1;
	Unit target2;
	Unit target3;

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.SHADOWFURY, 1);

		target1 = getEnemy("Target1");
		target2 = getEnemy("Target2");
		target3 = getEnemy("Target3");

		simulation.add(target1);
		simulation.add(target2);
		simulation.add(target3);
	}
}

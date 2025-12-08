package wow.simulator.simulation.spell.tbc.ability.mage.fire;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FLAMESTRIKE;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class FlamestrikeTest extends TbcMageSpellSimulationTest {
	/*
	Calls down a pillar of fire, burning all enemies within the area for 480 to 585 Fire damage and an additional 424 Fire damage over 8 sec.
	 */

	@Test
	void success() {
		player.cast(FLAMESTRIKE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, FLAMESTRIKE, 3)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, FLAMESTRIKE)
						.decreasedResource(1175, MANA, player, FLAMESTRIKE)
						.decreasedResource(532, HEALTH, target, FLAMESTRIKE)
						.decreasedResource(532, HEALTH, target2, FLAMESTRIKE)
						.decreasedResource(532, HEALTH, target3, FLAMESTRIKE)
						.decreasedResource(532, HEALTH, target4, FLAMESTRIKE)
						.effectApplied(FLAMESTRIKE, null, 8),
				at(5)
						.decreasedResource(106, HEALTH, target, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target2, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target3, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target4, FLAMESTRIKE),
				at(7)
						.decreasedResource(106, HEALTH, target, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target2, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target3, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target4, FLAMESTRIKE),
				at(9)
						.decreasedResource(106, HEALTH, target, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target2, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target3, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target4, FLAMESTRIKE),
				at(11)
						.decreasedResource(106, HEALTH, target, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target2, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target3, FLAMESTRIKE)
						.decreasedResource(106, HEALTH, target4, FLAMESTRIKE)
						.effectExpired(FLAMESTRIKE, null)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(FLAMESTRIKE, spellDamage);

		assertDamageDone(FLAMESTRIKE_INFO, target, spellDamage);
		assertDamageDone(FLAMESTRIKE_INFO, target2, spellDamage);
		assertDamageDone(FLAMESTRIKE_INFO, target3, spellDamage);
		assertDamageDone(FLAMESTRIKE_INFO, target4, spellDamage);
	}
}

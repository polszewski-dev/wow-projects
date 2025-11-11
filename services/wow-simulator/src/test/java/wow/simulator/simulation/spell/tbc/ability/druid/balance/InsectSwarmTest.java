package wow.simulator.simulation.spell.tbc.ability.druid.balance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.INSECT_SWARM;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class InsectSwarmTest extends TbcDruidSpellSimulationTest {
	/*
	The enemy target is swarmed by insects, decreasing their chance to hit by 2% and causing 792 Nature damage over 12 sec.
	 */

	@Test
	void success() {
		player.cast(INSECT_SWARM);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, INSECT_SWARM)
						.beginGcd(player)
						.endCast(player, INSECT_SWARM)
						.decreasedResource(175, MANA, player, INSECT_SWARM)
						.effectApplied(INSECT_SWARM, target, 12),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(132, HEALTH, target, INSECT_SWARM),
				at(4)
						.decreasedResource(132, HEALTH, target, INSECT_SWARM),
				at(6)
						.decreasedResource(132, HEALTH, target, INSECT_SWARM),
				at(8)
						.decreasedResource(132, HEALTH, target, INSECT_SWARM),
				at(10)
						.decreasedResource(132, HEALTH, target, INSECT_SWARM),
				at(12)
						.decreasedResource(132, HEALTH, target, INSECT_SWARM)
						.effectExpired(INSECT_SWARM, target)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(INSECT_SWARM, spellDamage);

		assertDamageDone(INSECT_SWARM_INFO, spellDamage);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.INSECT_SWARM, 1);
	}
}

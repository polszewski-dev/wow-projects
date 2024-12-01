package wow.simulator.simulation.spell.talent.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedCorruptionTest extends SpellSimulationTest {
	@Test
	void improvedCorruption() {
		enableTalent(IMPROVED_CORRUPTION, 5);

		player.cast(CORRUPTION);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(6)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(9)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(12)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(15)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(18)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}
}

package wow.simulator.simulation.spell.talent.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.IMMOLATE;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.IMPROVED_IMMOLATE;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedImmolateTest extends WarlockSpellSimulationTest {
	@Test
	void improvedImmolate() {
		enableTalent(IMPROVED_IMMOLATE, 5);

		player.cast(IMMOLATE);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, IMMOLATE)
						.decreasedResource(445, MANA, player, IMMOLATE)
						.decreasedResource(415, HEALTH, target, IMMOLATE)
						.effectApplied(IMMOLATE, target),
				at(5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(8)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(11)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(14)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(17)
						.decreasedResource(123, HEALTH, target, IMMOLATE)
						.effectExpired(IMMOLATE, target)
		);
	}
}

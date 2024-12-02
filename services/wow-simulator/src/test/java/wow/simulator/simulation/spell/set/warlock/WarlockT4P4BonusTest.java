package wow.simulator.simulation.spell.set.warlock;

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
class WarlockT4P4BonusTest extends SpellSimulationTest {
	@Test
	void t4Bonus() {
		enableTalent(IMPROVED_CORRUPTION, 5);

		equip("Voidheart Crown");
		equip("Voidheart Gloves");
		equip("Voidheart Leggings");
		equip("Voidheart Mantle");

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
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(6)
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(9)
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(12)
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(15)
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(18)
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(21)
						.decreasedResource(175, HEALTH, target, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}
}

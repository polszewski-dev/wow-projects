package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.commons.model.buff.BuffId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.EMPOWERED_CORRUPTION;
import static wow.commons.model.talent.TalentId.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class EmpoweredCorruptionTest extends WarlockSpellSimulationTest {
	@Test
	void empoweredCorruption() {
		enableTalent(IMPROVED_CORRUPTION, 5);
		enableTalent(EMPOWERED_CORRUPTION, 3);
		enableBuff(BuffId.FEL_ARMOR, 2);

		player.cast(CORRUPTION);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target, 18)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(171, HEALTH, target, CORRUPTION),
				at(6)
						.decreasedResource(172, HEALTH, target, CORRUPTION),
				at(9)
						.decreasedResource(171, HEALTH, target, CORRUPTION),
				at(12)
						.decreasedResource(172, HEALTH, target, CORRUPTION),
				at(15)
						.decreasedResource(171, HEALTH, target, CORRUPTION),
				at(18)
						.decreasedResource(172, HEALTH, target, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}
}

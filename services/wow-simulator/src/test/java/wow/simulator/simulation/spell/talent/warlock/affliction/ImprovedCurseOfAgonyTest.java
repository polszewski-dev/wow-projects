package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CURSE_OF_AGONY;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.IMPROVED_CURSE_OF_AGONY;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedCurseOfAgonyTest extends WarlockSpellSimulationTest {
	@Test
	void improvedCurseOfAgony() {
		enableTalent(IMPROVED_CURSE_OF_AGONY, 2);

		player.cast(CURSE_OF_AGONY);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CURSE_OF_AGONY)
						.endCast(player, CURSE_OF_AGONY)
						.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
						.effectApplied(CURSE_OF_AGONY, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(62, HEALTH, target, CURSE_OF_AGONY),
				at(4)
						.decreasedResource(62, HEALTH, target, CURSE_OF_AGONY),
				at(6)
						.decreasedResource(62, HEALTH, target, CURSE_OF_AGONY),
				at(8)
						.decreasedResource(62, HEALTH, target, CURSE_OF_AGONY),
				at(10)
						.decreasedResource(124, HEALTH, target, CURSE_OF_AGONY),
				at(12)
						.decreasedResource(125, HEALTH, target, CURSE_OF_AGONY),
				at(14)
						.decreasedResource(124, HEALTH, target, CURSE_OF_AGONY),
				at(16)
						.decreasedResource(124, HEALTH, target, CURSE_OF_AGONY),
				at(18)
						.decreasedResource(187, HEALTH, target, CURSE_OF_AGONY),
				at(20)
						.decreasedResource(186, HEALTH, target, CURSE_OF_AGONY),
				at(22)
						.decreasedResource(187, HEALTH, target, CURSE_OF_AGONY),
				at(24)
						.decreasedResource(186, HEALTH, target, CURSE_OF_AGONY)
						.effectExpired(CURSE_OF_AGONY, target)
		);
	}
}

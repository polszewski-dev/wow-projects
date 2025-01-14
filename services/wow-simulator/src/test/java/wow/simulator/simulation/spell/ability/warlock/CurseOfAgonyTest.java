package wow.simulator.simulation.spell.ability.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CURSE_OF_AGONY;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class CurseOfAgonyTest extends WarlockSpellSimulationTest {
	@Test
	void success() {
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
						.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
				at(4)
						.decreasedResource(57, HEALTH, target, CURSE_OF_AGONY),
				at(6)
						.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
				at(8)
						.decreasedResource(57, HEALTH, target, CURSE_OF_AGONY),
				at(10)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(12)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(14)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(16)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(18)
						.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
				at(20)
						.decreasedResource(170, HEALTH, target, CURSE_OF_AGONY),
				at(22)
						.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
				at(24)
						.decreasedResource(170, HEALTH, target, CURSE_OF_AGONY)
						.effectExpired(CURSE_OF_AGONY, target)
		);
	}

	@Test
	void resisted() {
		rng.hitRoll = false;

		player.cast(CURSE_OF_AGONY);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CURSE_OF_AGONY)
						.endCast(player, CURSE_OF_AGONY)
						.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
						.spellResisted(player, CURSE_OF_AGONY, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void interrupted() {
		player.cast(CURSE_OF_AGONY);

		simulation.updateUntil(Time.at(1));

		player.interruptCurrentAction();

		simulation.updateUntil(Time.at(10));

		player.interruptCurrentAction();

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
						.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
				at(4)
						.decreasedResource(57, HEALTH, target, CURSE_OF_AGONY),
				at(6)
						.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
				at(8)
						.decreasedResource(57, HEALTH, target, CURSE_OF_AGONY),
				at(10)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(12)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(14)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(16)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(18)
						.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
				at(20)
						.decreasedResource(170, HEALTH, target, CURSE_OF_AGONY),
				at(22)
						.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
				at(24)
						.decreasedResource(170, HEALTH, target, CURSE_OF_AGONY)
						.effectExpired(CURSE_OF_AGONY, target)
		);
	}
}

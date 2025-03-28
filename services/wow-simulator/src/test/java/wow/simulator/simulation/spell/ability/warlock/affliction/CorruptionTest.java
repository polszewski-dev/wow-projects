package wow.simulator.simulation.spell.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class CorruptionTest extends WarlockSpellSimulationTest {
	/*
	Corrupts the target, causing 900 Shadow damage over 18 sec.
	 */

	@Test
	void success() {
		player.cast(CORRUPTION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target, 18),
				at(5)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(8)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(11)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(14)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(17)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(20)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}

	@Test
	void resisted() {
		missesOnlyOnFollowingRolls(0);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.spellResisted(player, CORRUPTION, target)
		);
	}

	@Test
	void interrupted() {
		enableTalent(IMPROVED_CORRUPTION, 2);

		player.cast(CORRUPTION);

		updateUntil(1);

		player.interruptCurrentAction();

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION, 1.2)
						.beginGcd(player),
				at(1)
						.castInterrupted(player, CORRUPTION)
						.endGcd(player)
		);
	}

	@Test
	void damageDone() {
		player.cast(CORRUPTION);

		updateUntil(30);

		assertDamageDone(CORRUPTION, CORRUPTION_INFO.damage());
	}
}

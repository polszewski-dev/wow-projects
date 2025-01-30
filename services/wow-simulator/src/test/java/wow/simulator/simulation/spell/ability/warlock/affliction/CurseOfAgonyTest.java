package wow.simulator.simulation.spell.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CURSE_OF_AGONY;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class CurseOfAgonyTest extends WarlockSpellSimulationTest {
	/*
	Curses the target with agony, causing 1356 Shadow damage over 24 sec.
	This damage is dealt slowly at first, and builds up as the Curse reaches its full duration.
	 Only one Curse per Warlock can be active on any one target.
	 */

	@Test
	void success() {
		player.cast(CURSE_OF_AGONY);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CURSE_OF_AGONY)
						.endCast(player, CURSE_OF_AGONY)
						.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
						.effectApplied(CURSE_OF_AGONY, target, 24)
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
		missesOnlyOnFollowingRolls(0);

		player.cast(CURSE_OF_AGONY);

		updateUntil(30);

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

		updateUntil(1);

		player.interruptCurrentAction();

		updateUntil(10);

		player.interruptCurrentAction();

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CURSE_OF_AGONY)
						.endCast(player, CURSE_OF_AGONY)
						.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
						.effectApplied(CURSE_OF_AGONY, target, 24)
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
	void damageDone() {
		player.cast(CURSE_OF_AGONY);

		updateUntil(30);

		assertDamageDone(CURSE_OF_AGONY, CURSE_OF_AGONY_INFO.damage());
	}
}

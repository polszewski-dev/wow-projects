package wow.simulator.simulation;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2025-12-08
 */
class SpellInterruptTest extends TbcWarlockSpellSimulationTest {
	@Test
	void cast_direct_spell_interrupted() {
		player.cast(SHADOW_BOLT);

		runAt(1, player::interruptCurrentAction);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(1)
						.castInterrupted(player, SHADOW_BOLT)
						.endGcd(player)
		);
	}

	@Test
	void cast_periodic_spell_interrupted() {
		enableTalent(IMPROVED_CORRUPTION, 2);

		player.cast(CORRUPTION);

		runAt(1, player::interruptCurrentAction);

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
	void channelled_spell_interrupted() {
		setHealth(player, 1000);

		player.cast(DRAIN_LIFE);

		runAt(1.25, player::interruptCurrentAction);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, DRAIN_LIFE)
						.beginGcd(player)
						.endCast(player, DRAIN_LIFE)
						.decreasedResource(425, MANA, player, DRAIN_LIFE)
						.effectApplied(DRAIN_LIFE, target, 5)
						.beginChannel(player, DRAIN_LIFE, 5),
				at(1)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(1.25)
						.channelInterrupted(player, DRAIN_LIFE)
						.effectRemoved(DRAIN_LIFE, target),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void instant_direct_spell_can_not_be_interrupted() {
		enableTalent(SHADOWBURN, 1);

		player.cast(SHADOWBURN);

		runAt(1, player::interruptCurrentAction);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOWBURN)
						.beginGcd(player)
						.endCast(player, SHADOWBURN)
						.decreasedResource(515, MANA, player, SHADOWBURN)
						.cooldownStarted(player, SHADOWBURN, 15)
						.decreasedResource(631, HEALTH, target, SHADOWBURN),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, SHADOWBURN)
		);
	}

	@Test
	void instant_periodic_spell_can_not_be_interrupted() {
		player.cast(CURSE_OF_AGONY);

		runAt(1, player::interruptCurrentAction);
		runAt(10, player::interruptCurrentAction);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CURSE_OF_AGONY)
						.beginGcd(player)
						.endCast(player, CURSE_OF_AGONY)
						.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
						.effectApplied(CURSE_OF_AGONY, target, 24),
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
	void cast_spell_with_direct_and_periodic_component_interrupted() {
		player.cast(IMMOLATE);

		runAt(1, player::interruptCurrentAction);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE, 2)
						.beginGcd(player),
				at(1)
						.castInterrupted(player, IMMOLATE)
						.endGcd(player)
		);
	}
}

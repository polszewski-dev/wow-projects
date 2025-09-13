package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.TALENT;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.IMPROVED_CORRUPTION;
import static wow.test.commons.TalentNames.NIGHTFALL;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class NightfallTest extends WarlockSpellSimulationTest {
	/*
	Gives your Corruption and Drain Life spells a 4% chance to cause you to enter a Shadow Trance state after damaging the opponent.
	The Shadow Trance state reduces the casting time of your next Shadow Bolt spell by 100%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void procIsTriggeredByCorruption(int rank) {
		enableTalent(NIGHTFALL, rank);
		enableTalent(IMPROVED_CORRUPTION, 5);

		eventsOnlyOnFollowingRolls(0);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION)
						.beginGcd(player)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, false, player, CORRUPTION)
						.effectApplied(CORRUPTION, target, 18),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectApplied(NIGHTFALL, TALENT, player, 10),
				at(6)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(9)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(12)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(13)
						.effectExpired(NIGHTFALL, TALENT, player),
				at(15)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(18)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void procIsTriggeredByDrainLife(int rank) {
		enableTalent(NIGHTFALL, rank);

		eventsOnlyOnFollowingRolls(0);

		player.cast(DRAIN_LIFE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, DRAIN_LIFE)
						.beginGcd(player)
						.endCast(player, DRAIN_LIFE)
						.decreasedResource(425, MANA, false, player, DRAIN_LIFE)
						.effectApplied(DRAIN_LIFE, target, 5)
						.beginChannel(player, DRAIN_LIFE, 5),
				at(1)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.effectApplied(NIGHTFALL, TALENT, player, 10)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(3)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(4)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(5)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE)
						.effectExpired(DRAIN_LIFE, target)
						.endChannel(player, DRAIN_LIFE),
				at(11)
						.effectExpired(NIGHTFALL, TALENT, player)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void correctProcChance(int rank) {
		enableTalent(NIGHTFALL, rank);

		eventsOnlyOnFollowingRolls(0);

		player.cast(DRAIN_LIFE);

		updateUntil(30);

		assertEventChanceNo(0, 2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void effectIsRemovedAfterShadowBoltCast(int rank) {
		enableTalent(NIGHTFALL, rank);
		enableTalent(IMPROVED_CORRUPTION, 5);

		eventsOnlyOnFollowingRolls(0);

		player.cast(CORRUPTION);
		player.idleUntil(Time.at(4));
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION)
						.beginGcd(player)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, false, player, CORRUPTION)
						.effectApplied(CORRUPTION, target, 18),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectApplied(NIGHTFALL, TALENT, player, 10),
				at(4)
						.beginCast(player, SHADOW_BOLT, 0)
						.beginGcd(player)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.effectRemoved(NIGHTFALL, TALENT, player)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT),
				at(5.5)
						.endGcd(player),
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

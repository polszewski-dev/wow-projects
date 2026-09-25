package wow.simulator.simulation.spell.tbc.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.simulator.util.TestEvent;

import java.util.List;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-12-23
 */
class SeedOfCorruptionTest extends TbcWarlockSpellSimulationTest {
	/*
	Imbeds a demon seed in the enemy target, causing 1044 Shadow damage over 18 sec.
	When the target takes 1044 total damage or dies, the seed will inflict 1110 to 1290 Shadow damage to all other enemies within 15 yards of the target.
	Only one Corruption spell per Warlock can be active on any one target.
	 */

	@Test
	void success() {
		player.cast(SEED_OF_CORRUPTION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SEED_OF_CORRUPTION, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, SEED_OF_CORRUPTION)
						.decreasedResource(882, MANA, player, SEED_OF_CORRUPTION)
						.effectApplied(SEED_OF_CORRUPTION, target, 18),
				at(5)
						.decreasedResource(174, HEALTH, target, SEED_OF_CORRUPTION),
				at(8)
						.decreasedResource(174, HEALTH, target, SEED_OF_CORRUPTION),
				at(11)
						.decreasedResource(174, HEALTH, target, SEED_OF_CORRUPTION),
				at(14)
						.decreasedResource(174, HEALTH, target, SEED_OF_CORRUPTION),
				at(17)
						.decreasedResource(174, HEALTH, target, SEED_OF_CORRUPTION),
				at(20)
						.decreasedResource(174, HEALTH, target, SEED_OF_CORRUPTION)
						.effectRemoved(SEED_OF_CORRUPTION, target)
						.decreasedResource(1200, HEALTH, target2, SEED_OF_CORRUPTION)
						.decreasedResource(1200, HEALTH, target3, SEED_OF_CORRUPTION)
						.decreasedResource(1200, HEALTH, target4, SEED_OF_CORRUPTION)
						.decreasedResource(1200, HEALTH, target5, SEED_OF_CORRUPTION)
		);
	}

	@Test
	void bonus_sp_causes_faster_explosion() {
		addSpBonus(1000);

		player.cast(SEED_OF_CORRUPTION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SEED_OF_CORRUPTION, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, SEED_OF_CORRUPTION)
						.decreasedResource(882, MANA, player, SEED_OF_CORRUPTION)
						.effectApplied(SEED_OF_CORRUPTION, target, 18),
				at(5)
						.decreasedResource(424, HEALTH, target, SEED_OF_CORRUPTION),
				at(8)
						.decreasedResource(424, HEALTH, target, SEED_OF_CORRUPTION),
				at(11)
						.decreasedResource(424, HEALTH, target, SEED_OF_CORRUPTION)
						.effectRemoved(SEED_OF_CORRUPTION, target)
						.decreasedResource(1420, HEALTH, target2, SEED_OF_CORRUPTION)
						.decreasedResource(1420, HEALTH, target3, SEED_OF_CORRUPTION)
						.decreasedResource(1420, HEALTH, target4, SEED_OF_CORRUPTION)
						.decreasedResource(1420, HEALTH, target5, SEED_OF_CORRUPTION)
		);
	}

	@Test
	void damage_from_players_other_spells_causes_faster_explosion() {
		player.cast(SEED_OF_CORRUPTION);
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SEED_OF_CORRUPTION, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, SEED_OF_CORRUPTION)
						.decreasedResource(882, MANA, player, SEED_OF_CORRUPTION)
						.effectApplied(SEED_OF_CORRUPTION, target, 18)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(3.5)
						.endGcd(player),
				at(5)
						.decreasedResource(174, HEALTH, target, SEED_OF_CORRUPTION)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT),
				at(8)
						.decreasedResource(174, HEALTH, target, SEED_OF_CORRUPTION),
				at(11)
						.decreasedResource(174, HEALTH, target, SEED_OF_CORRUPTION)
						.effectRemoved(SEED_OF_CORRUPTION, target)
						.decreasedResource(1200, HEALTH, target2, SEED_OF_CORRUPTION)
						.decreasedResource(1200, HEALTH, target3, SEED_OF_CORRUPTION)
						.decreasedResource(1200, HEALTH, target4, SEED_OF_CORRUPTION)
						.decreasedResource(1200, HEALTH, target5, SEED_OF_CORRUPTION)
		);
	}

	@Test
	void damage_from_other_players_spells_causes_faster_explosion() {
		player.cast(SEED_OF_CORRUPTION);

		player2.setTarget(target);
		player2.cast(SHADOW_BOLT);
		player2.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SEED_OF_CORRUPTION, 2)
						.beginGcd(player)
						.beginCast(player2, SHADOW_BOLT, 3)
						.beginGcd(player2),
				at(1.5)
						.endGcd(player)
						.endGcd(player2),
				at(2)
						.endCast(player, SEED_OF_CORRUPTION)
						.decreasedResource(882, MANA, player, SEED_OF_CORRUPTION)
						.effectApplied(SEED_OF_CORRUPTION, target, 18),

				at(3)
						.endCast(player2, SHADOW_BOLT)
						.decreasedResource(420, MANA, player2, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
						.beginCast(player2, SHADOW_BOLT, 3)
						.beginGcd(player2),
				at(4.5)
						.endGcd(player2),
				at(5)
						.decreasedResource(174, HEALTH, target, SEED_OF_CORRUPTION),
				at(6)
						.endCast(player2, SHADOW_BOLT)
						.decreasedResource(420, MANA, player2, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
						.effectRemoved(SEED_OF_CORRUPTION, target)
						.decreasedResource(1200, HEALTH, target2, SEED_OF_CORRUPTION)
						.decreasedResource(1200, HEALTH, target3, SEED_OF_CORRUPTION)
						.decreasedResource(1200, HEALTH, target4, SEED_OF_CORRUPTION)
						.decreasedResource(1200, HEALTH, target5, SEED_OF_CORRUPTION)
		);
	}

	@Test
	void soc_spam_script() {
		enableTalent(SHADOWBURN);

		player.setScript("warlock-soc-spam");

		var scenario = getScenario(40);

		scenario.execute(
				player.getRaid(),
				List.of(target, target2, target3, target4, target5),
				List.of(handler)
		);

		assertEvents(
				TestEvent::isBeginCast,
				at(60).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(62).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(64).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(66).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(68).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(70).beginCast(player, SHADOWBURN),
				at(71.5).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(73.5).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(75.5).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(77.5).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(79.5).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(81.5).beginCast(player, SHADOW_BOLT, 3),
				at(84.5).beginCast(player, SHADOW_BOLT, 3),
				at(87.5).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(89.5).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(91.5).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(93.5).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(95.5).beginCast(player, SEED_OF_CORRUPTION, 2),
				at(97.5).beginCast(player, SHADOWBURN)
		);
	}
}

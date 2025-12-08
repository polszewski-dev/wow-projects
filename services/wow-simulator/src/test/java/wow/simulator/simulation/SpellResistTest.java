package wow.simulator.simulation;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-12-08
 */
class SpellResistTest extends TbcWarlockSpellSimulationTest {
	@Test
	void direct_spell_with_cast_time_resisted() {
		missesOnlyOnFollowingRolls(0);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.spellResisted(player, SHADOW_BOLT, target)
		);
	}

	@Test
	void periodic_spell_with_cast_time_resisted() {
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
	void channelled_spell_resisted() {
		missesOnlyOnFollowingRolls(0);

		player.cast(DRAIN_LIFE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, DRAIN_LIFE)
						.beginGcd(player)
						.endCast(player, DRAIN_LIFE)
						.decreasedResource(425, MANA, player, DRAIN_LIFE)
						.spellResisted(player, DRAIN_LIFE, target),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void instant_direct_spell_resisted() {
		enableTalent(SHADOWBURN, 1);

		missesOnlyOnFollowingRolls(0);

		player.cast(SHADOWBURN);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOWBURN)
						.beginGcd(player)
						.endCast(player, SHADOWBURN)
						.decreasedResource(515, MANA, player, SHADOWBURN)
						.cooldownStarted(player, SHADOWBURN, 15)
						.spellResisted(player, SHADOWBURN, target),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, SHADOWBURN)
		);
	}

	@Test
	void instant_periodic_spell_resisted() {
		missesOnlyOnFollowingRolls(0);

		player.cast(CURSE_OF_AGONY);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CURSE_OF_AGONY)
						.beginGcd(player)
						.endCast(player, CURSE_OF_AGONY)
						.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
						.spellResisted(player, CURSE_OF_AGONY, target),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void cast_spell_with_direct_and_periodic_component_resisted() {
		missesOnlyOnFollowingRolls(0);

		player.cast(IMMOLATE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, IMMOLATE)
						.decreasedResource(445, MANA, player, IMMOLATE)
						.spellResisted(player, IMMOLATE, target)
		);
	}
}

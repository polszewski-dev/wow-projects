package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.HOLY_SHOCK;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class HolyShockTest extends TbcPaladinSpellSimulationTest {
	/*
	Blasts the target with Holy energy, causing 721 to 779 Holy damage to an enemy, or 913 to 987 healing to an ally.
	 */

	@Test
	void cast_on_enemy() {
		setHealth(target, 1000);
		player.cast(HOLY_SHOCK, target);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, HOLY_SHOCK)
						.beginGcd(player)
						.endCast(player, HOLY_SHOCK)
						.decreasedResource(650, MANA, player, HOLY_SHOCK)
						.cooldownStarted(player, HOLY_SHOCK, 15)
						.decreasedResource(750, HEALTH, target, HOLY_SHOCK),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, HOLY_SHOCK)
		);
	}

	@Test
	void cast_on_friend() {
		setHealth(player2, 1000);
		player.cast(HOLY_SHOCK, player2);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, HOLY_SHOCK)
						.beginGcd(player)
						.endCast(player, HOLY_SHOCK)
						.decreasedResource(650, MANA, player, HOLY_SHOCK)
						.cooldownStarted(player, HOLY_SHOCK, 15)
						.increasedResource(950, HEALTH, player2, HOLY_SHOCK),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, HOLY_SHOCK)
		);
	}

	@Test
	void cast_on_self() {
		setHealth(player, 1000);
		player.cast(HOLY_SHOCK, player);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, HOLY_SHOCK)
						.beginGcd(player)
						.endCast(player, HOLY_SHOCK)
						.decreasedResource(650, MANA, player, HOLY_SHOCK)
						.cooldownStarted(player, HOLY_SHOCK, 15)
						.increasedResource(950, HEALTH, player, HOLY_SHOCK),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, HOLY_SHOCK)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int sp) {
		addSpBonus(sp);
		setHealth(target, 3000);

		player.cast(HOLY_SHOCK, target);

		updateUntil(30);

		assertDamageDone(HOLY_SHOCK_INFO, target, sp);
		assertHealthGained(HOLY_SHOCK, target, 0);
	}

	@ParameterizedTest
	@MethodSource("spellHealingLevels")
	void healing_done(int healing) {
		addHealingBonus(healing);
		setHealth(player2, 2000);

		player.cast(HOLY_SHOCK, player2);

		updateUntil(30);

		assertDamageDone(HOLY_SHOCK, player2, 0);
		assertHealthGained(HOLY_SHOCK_HEALING_PART_INFO, player2, healing);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.HOLY_SHOCK, 1);
	}
}

package wow.simulator.simulation.spell.tbc.ability.mage.fire;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.test.commons.AbilityNames.COMBUSTION;
import static wow.test.commons.AbilityNames.FIREBALL;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class CombustionTest extends TbcMageSpellSimulationTest {
	/*
	When activated, this spell causes each of your Fire damage spell hits to increase your critical strike chance with Fire damage spells by 10%.
	This effect lasts until you have caused 3 critical strikes with Fire spells.
	 */

	@Test
	void success() {
		player.cast(COMBUSTION);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, COMBUSTION)
						.endCast(player, COMBUSTION)
						.cooldownStarted(player, COMBUSTION, 180)
						.effectApplied(COMBUSTION, player, Duration.INFINITE),
				at(180)
						.cooldownExpired(player, COMBUSTION)
		);
	}

	@Test
	void crit_chance_increases_each_time_theres_no_crit_until_reaching_100_pct() {
		player.addHiddenEffect("Bonus Mp5", 500);

		player.cast(FIREBALL);
		player.cast(COMBUSTION);

		for (int i = 0; i < 14; ++i) {
			player.cast(FIREBALL);
		}

		updateUntil(60);

		var baseCritChance = rng.getCritRollData().getRollChances().getFirst();

		assertCritChanceNo(1, baseCritChance);
		assertCritChanceNo(2, baseCritChance + 10);
		assertCritChanceNo(3, baseCritChance + 20);
		assertCritChanceNo(4, baseCritChance + 30);
		assertCritChanceNo(5, baseCritChance + 40);
		assertCritChanceNo(6, baseCritChance + 50);
		assertCritChanceNo(7, baseCritChance + 60);
		assertCritChanceNo(8, baseCritChance + 70);
		assertCritChanceNo(9, baseCritChance + 80);
		assertCritChanceNo(10, baseCritChance + 90);
		assertCritChanceNo(11, 100);
		assertCritChanceNo(12, 100);
		assertCritChanceNo(13, 100);
		assertCritChanceNo(14, baseCritChance);
	}

	@Test
	void crit_chance_increases_each_time_theres_no_crit_before_reaching_100_pct() {
		player.addHiddenEffect("Bonus Mp5", 500);

		critsOnlyOnFollowingRolls(2, 3, 4);

		player.cast(FIREBALL);
		player.cast(COMBUSTION);

		for (int i = 0; i < 5; ++i) {
			player.cast(FIREBALL);
		}

		updateUntil(60);

		var baseCritChance = rng.getCritRollData().getRollChances().getFirst();

		assertCritChanceNo(1, baseCritChance);
		assertCritChanceNo(2, baseCritChance + 10);
		assertCritChanceNo(3, baseCritChance + 20);
		assertCritChanceNo(4, baseCritChance + 30);
		assertCritChanceNo(5, baseCritChance);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.COMBUSTION, 1);
	}
}

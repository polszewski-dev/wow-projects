package wow.simulator.simulation.spell.tbc.set.mage;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ITEM_SET;
import static wow.test.commons.AbilityNames.SCORCH;

/**
 * User: POlszewski
 * Date: 2026-09-06
 */
class T3P6BonusTest extends TbcMageSpellSimulationTest {
	/*
	Your damage spells have a chance to cause your target to take up to 200 increased damage from subsequent spells.
	 */
	@Test
	void proc_is_triggered() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(SCORCH);
		player.cast(SCORCH);
		player.cast(SCORCH);

		updateUntil(30);

		assertEvents(
			at(0)
					.beginCast(player, SCORCH, 1.5)
					.beginGcd(player),
			at(1.5)
					.endCast(player, SCORCH)
					.decreasedResource(180, MANA, player, SCORCH)
					.decreasedResource(426, HEALTH, target, SCORCH)
					.effectApplied("Frostfire Regalia - P6 bonus - triggered", ITEM_SET, target, 30)
					.endGcd(player)
					.beginCast(player, SCORCH, 1.5)
					.beginGcd(player),
			at(3)
					.endCast(player, SCORCH)
					.decreasedResource(180, MANA, player, SCORCH)
					.effectRemoved("Frostfire Regalia - P6 bonus - triggered", ITEM_SET, target)
					.decreasedResource(512, HEALTH, target, SCORCH)
					.endGcd(player)
					.beginCast(player, SCORCH, 1.5)
					.beginGcd(player),
			at(4.5)
					.endCast(player, SCORCH)
					.decreasedResource(180, MANA, player, SCORCH)
					.decreasedResource(426, HEALTH, target, SCORCH)
					.endGcd(player)
		);
	}

	@Test
	void players_next_spell_damage_is_increased() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(SCORCH);
		player.cast(SCORCH);
		player.cast(SCORCH);

		updateUntil(30);

		var sd = player.getStats().getSpellDamage();

		assertDamageDone(0, SCORCH_INFO, target, player, sd, 0);
		assertDamageDone(1, SCORCH_INFO, target, player, sd + 200, 0);
		assertDamageDone(2, SCORCH_INFO, target, player, sd, 0);
	}

	@Test
	void other_players_next_spell_damage_is_increased() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(SCORCH);
		player.cast(SCORCH);

		player2.idleUntil(Time.at(1));
		player2.cast(SCORCH);
		player2.cast(SCORCH);

		updateUntil(30);

		var sd = player.getStats().getSpellDamage();

		assertDamageDone(0, SCORCH_INFO, target, player, sd, 0);
		assertDamageDone(1, SCORCH_INFO, target, player, sd, 0);

		assertDamageDone(0, SCORCH_INFO, target, player2, 200, 0);
		assertDamageDone(1, SCORCH_INFO, target, player2, 0, 0);
	}

	@Override
	protected void afterSetUp() {
		equip("Frostfire Belt");
		equip("Frostfire Bindings");
		equip("Frostfire Circlet");
		equip("Frostfire Gloves");
		equip("Frostfire Leggings");
		equip("Frostfire Robe");

		setTargetForAllPlayers(target);
	}
}

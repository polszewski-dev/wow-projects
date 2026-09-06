package wow.simulator.simulation.spell.tbc.set.mage;

import org.junit.jupiter.api.Test;
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
					.effectApplied("Frostfire Regalia - P6 bonus - triggered", ITEM_SET, target, 30)
					.decreasedResource(512, HEALTH, target, SCORCH)
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


	@Override
	protected void afterSetUp() {
		equip("Frostfire Belt");
		equip("Frostfire Bindings");
		equip("Frostfire Circlet");
		equip("Frostfire Gloves");
		equip("Frostfire Leggings");
		equip("Frostfire Robe");
	}
}

package wow.simulator.simulation.spell.tbc.set.mage;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ITEM_SET;
import static wow.test.commons.AbilityNames.FROSTBOLT;

/**
 * User: POlszewski
 * Date: 2026-09-06
 */
class T2P8BonusTest extends TbcMageSpellSimulationTest {
	/*
	10% chance after casting Arcane Missiles, Fireball, or Frostbolt that your next spell with a casting time under 10 seconds cast instantly.
	 */
	@Test
	void proc_is_triggered() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(FROSTBOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, FROSTBOLT, 3)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, FROSTBOLT)
						.decreasedResource(345, MANA, player, FROSTBOLT)
						.effectApplied("Netherwind Regalia - P8 bonus - triggered", ITEM_SET, player, 10)
						.decreasedResource(821, HEALTH, target, FROSTBOLT),
				at(13)
						.effectExpired("Netherwind Regalia - P8 bonus - triggered", ITEM_SET, player)

		);
	}

	@Test
	void proc_is_triggered_and_consumed() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, FROSTBOLT, 3)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, FROSTBOLT)
						.decreasedResource(345, MANA, player, FROSTBOLT)
						.effectApplied("Netherwind Regalia - P8 bonus - triggered", ITEM_SET, player, 10)
						.decreasedResource(821, HEALTH, target, FROSTBOLT)
						.beginCast(player, FROSTBOLT)
						.beginGcd(player)
						.endCast(player, FROSTBOLT)
						.decreasedResource(345, MANA, player, FROSTBOLT)
						.effectRemoved("Netherwind Regalia - P8 bonus - triggered", ITEM_SET, player)
						.decreasedResource(821, HEALTH, target, FROSTBOLT),
				at(4.5)
						.endGcd(player)

		);
	}

	@Override
	protected void afterSetUp() {
		equip("Netherwind Belt");
		equip("Netherwind Bindings");
		equip("Netherwind Boots");
		equip("Netherwind Crown");
		equip("Netherwind Mantle");
		equip("Netherwind Gloves");
		equip("Netherwind Pants");
		equip("Netherwind Robes");
	}
}

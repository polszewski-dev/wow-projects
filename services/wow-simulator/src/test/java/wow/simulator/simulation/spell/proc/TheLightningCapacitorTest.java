package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.SpellId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ITEM;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2024-12-02
 */
class TheLightningCapacitorTest extends WarlockSpellSimulationTest {
	/*
	Equip: You gain an Electrical Charge each time you cause a damaging spell critical strike.
	When you reach 3 Electrical Charges, they will release, firing a Lightning Bolt for 694 to 806 damage.
	Electrical Charge cannot be gained more often than once every 2.5 sec. (2.5s cooldown)
	 */
	@Test
	void procIsTriggered() {
		critsOnlyOnFollowingRolls(0);

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
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.cooldownStarted(player, cooldownId, 2.5)
						.effectApplied("The Lightning Capacitor", ITEM, player, Duration.INFINITE),
				at(5.5)
						.cooldownExpired(player, cooldownId)
		);
	}

	@Test
	void thirdProcTriggersDamageSpell() {
		critsOnlyOnFollowingRolls(0, 1, 2);

		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				event -> event.isDamage() || event.isEffect(),
				at(3)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectApplied("The Lightning Capacitor", ITEM, player, Duration.INFINITE),
				at(6)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectStacked("The Lightning Capacitor", ITEM, player, 2),
				at(9)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectStacked("The Lightning Capacitor", ITEM, player, 3)
						.effectRemoved("The Lightning Capacitor", ITEM, player)
						.decreasedResource(750, HEALTH, player, "The Lightning Capacitor - proc #1 - triggered - triggered")//todo wrong target
		);
	}

	CooldownId cooldownId = CooldownId.of(SpellId.of(100128785));

	@Override
	protected void afterSetUp() {
		equip("The Lightning Capacitor", TRINKET_1);
	}
}

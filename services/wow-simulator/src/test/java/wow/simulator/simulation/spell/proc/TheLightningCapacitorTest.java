package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.CooldownId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-12-02
 */
class TheLightningCapacitorTest extends SpellSimulationTest {
	/*
	Equip: You gain an Electrical Charge each time you cause a damaging spell critical strike.
	When you reach 3 Electrical Charges, they will release, firing a Lightning Bolt for 694 to 806 damage.
	Electrical Charge cannot be gained more often than once every 2.5 sec. (2.5s cooldown)
	 */
	@Test
	void procIsTriggered() {
		rng.critRoll = true;
		rng.eventRoll = true;

		player.cast(AbilityId.SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.cooldownStarted(player, cooldownId)
						.effectApplied("The Lightning Capacitor", player),
				at(5.5)
						.cooldownExpired(player, cooldownId)
		);
	}

	@Test
	void thirdProcTriggersDamageSpell() {
		rng.critRoll = true;
		rng.eventRoll = true;

		player.cast(AbilityId.SHADOW_BOLT);
		player.cast(AbilityId.SHADOW_BOLT);
		player.cast(AbilityId.SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				event -> event.isDamage() || event.isEffect(),
				at(3)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectApplied("The Lightning Capacitor", player),
				at(6)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectStacked("The Lightning Capacitor", player, 2),
				at(9)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.effectStacked("The Lightning Capacitor", player, 3)
						.effectRemoved("The Lightning Capacitor", player)
						.decreasedResource(1125, HEALTH, true, player, "The Lightning Capacitor - proc #1 - triggered - triggered")//todo wrong target
		);
	}

	CooldownId cooldownId = CooldownId.of(100128785);

	@BeforeEach
	public void setUp() {
		super.setUp();

		equip("The Lightning Capacitor", TRINKET_1);
	}
}

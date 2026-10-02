package wow.simulator.simulation.spell.tbc.proc;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.SpellId;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ITEM;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.EffectNames.ELECTRICAL_CHARGE;

/**
 * User: POlszewski
 * Date: 2024-12-02
 */
class TheLightningCapacitorTest extends TbcMageSpellSimulationTest {
	/*
	Equip: You gain an Electrical Charge each time you cause a damaging spell critical strike.
	When you reach 3 Electrical Charges, they will release, firing a Lightning Bolt for 694 to 806 damage.
	Electrical Charge cannot be gained more often than once every 2.5 sec. (2.5s cooldown)
	 */
	@Test
	void proc_is_triggered() {
		critsOnlyOnFollowingRolls(0);

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
						.decreasedResource(982, HEALTH, true, target, FROSTBOLT)
						.effectApplied(ELECTRICAL_CHARGE, ITEM, player, Duration.INFINITE)
		);
	}

	@Test
	void third_crit_fires_lightning_bolt() {
		critsOnlyOnFollowingRolls(0, 1, 2);

		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);

		updateUntil(30);

		assertEvents(
				event -> event.isDamage() || event.isEffect() || event.isCooldown(),
				at(3)
						.decreasedResource(982, HEALTH, true, target, FROSTBOLT)
						.effectApplied(ELECTRICAL_CHARGE, ITEM, player, Duration.INFINITE),
				at(6)
						.decreasedResource(982, HEALTH, true, target, FROSTBOLT)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 2),
				at(9)
						.decreasedResource(982, HEALTH, true, target, FROSTBOLT)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 3)
						.effectRemoved(ELECTRICAL_CHARGE, ITEM, player)
						.cooldownStarted(player, cooldownId, 2.5)
						.decreasedResource(750, HEALTH, target, LIGHTNING_BOLT),
				at(11.5)
						.cooldownExpired(player, cooldownId)
		);
	}

	@Test
	void arcane_explosion_3_crits() {
		critsOnlyOnFollowingRolls(0, 1, 2);

		player.cast(ARCANE_EXPLOSION);

		updateUntil(30);

		assertEvents(
				event -> event.isDamage() || event.isEffect() || event.isCooldown(),
				at(0)
						.decreasedResource(588, HEALTH, true, target, ARCANE_EXPLOSION)
						.effectApplied(ELECTRICAL_CHARGE, ITEM, player, Duration.INFINITE)
						.decreasedResource(588, HEALTH, true, target2, ARCANE_EXPLOSION)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 2)
						.decreasedResource(588, HEALTH, true, target3, ARCANE_EXPLOSION)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 3)
						.effectRemoved(ELECTRICAL_CHARGE, ITEM, player)
						.cooldownStarted(player, cooldownId, 2.5)
						.decreasedResource(750, HEALTH, target3, LIGHTNING_BOLT)
						.decreasedResource(392, HEALTH, false, target4, ARCANE_EXPLOSION)
						.decreasedResource(392, HEALTH, false, target5, ARCANE_EXPLOSION),
				at(2.5)
						.cooldownExpired(player, cooldownId)
		);
	}

	@Test
	void arcane_explosion_6_crits_2nd_bolt_not_fired_due_to_cooldown() {
		critsOnlyOnFollowingRolls(0, 1, 2, 6, 7, 8);

		player.cast(ARCANE_EXPLOSION);
		player.cast(ARCANE_EXPLOSION);

		updateUntil(30);

		assertEvents(
				event -> event.isDamage() || event.isEffect() || event.isCooldown(),
				at(0)
						.decreasedResource(588, HEALTH, true, target, ARCANE_EXPLOSION)
						.effectApplied(ELECTRICAL_CHARGE, ITEM, player, Duration.INFINITE)
						.decreasedResource(588, HEALTH, true, target2, ARCANE_EXPLOSION)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 2)
						.decreasedResource(588, HEALTH, true, target3, ARCANE_EXPLOSION)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 3)
						.effectRemoved(ELECTRICAL_CHARGE, ITEM, player)
						.cooldownStarted(player, cooldownId, 2.5)
						.decreasedResource(750, HEALTH, target3, LIGHTNING_BOLT)
						.decreasedResource(392, HEALTH, false, target4, ARCANE_EXPLOSION)
						.decreasedResource(392, HEALTH, false, target5, ARCANE_EXPLOSION),
				at(1.5)
						.decreasedResource(588, HEALTH, true, target, ARCANE_EXPLOSION)
						.effectApplied(ELECTRICAL_CHARGE, ITEM, player, Duration.INFINITE)
						.decreasedResource(588, HEALTH, true, target2, ARCANE_EXPLOSION)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 2)
						.decreasedResource(588, HEALTH, true, target3, ARCANE_EXPLOSION)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 3)
						.decreasedResource(392, HEALTH, false, target4, ARCANE_EXPLOSION)
						.decreasedResource(392, HEALTH, false, target5, ARCANE_EXPLOSION),
				at(2.5)
						.cooldownExpired(player, cooldownId)
		);
	}

	@Test
	void arcane_explosion_9_crits_1st_and_3rd_bolt_fired() {
		critsOnlyOnFollowingRolls(0, 1, 2, 6, 7, 8, 12);

		player.cast(ARCANE_EXPLOSION);
		player.cast(ARCANE_EXPLOSION);
		player.cast(ARCANE_EXPLOSION);

		updateUntil(30);

		assertEvents(
				event -> event.isDamage() || event.isEffect() || event.isCooldown(),
				at(0)
						.decreasedResource(588, HEALTH, true, target, ARCANE_EXPLOSION)
						.effectApplied(ELECTRICAL_CHARGE, ITEM, player, Duration.INFINITE)
						.decreasedResource(588, HEALTH, true, target2, ARCANE_EXPLOSION)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 2)
						.decreasedResource(588, HEALTH, true, target3, ARCANE_EXPLOSION)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 3)
						.effectRemoved(ELECTRICAL_CHARGE, ITEM, player)
						.cooldownStarted(player, cooldownId, 2.5)
						.decreasedResource(750, HEALTH, target3, LIGHTNING_BOLT)
						.decreasedResource(392, HEALTH, false, target4, ARCANE_EXPLOSION)
						.decreasedResource(392, HEALTH, false, target5, ARCANE_EXPLOSION),
				at(1.5)
						.decreasedResource(588, HEALTH, true, target, ARCANE_EXPLOSION)
						.effectApplied(ELECTRICAL_CHARGE, ITEM, player, Duration.INFINITE)
						.decreasedResource(588, HEALTH, true, target2, ARCANE_EXPLOSION)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 2)
						.decreasedResource(588, HEALTH, true, target3, ARCANE_EXPLOSION)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 3)
						.decreasedResource(392, HEALTH, false, target4, ARCANE_EXPLOSION)
						.decreasedResource(392, HEALTH, false, target5, ARCANE_EXPLOSION),
				at(2.5)
						.cooldownExpired(player, cooldownId),
				at(3)
						.decreasedResource(392, HEALTH, false, target, ARCANE_EXPLOSION)
						.decreasedResource(588, HEALTH, true, target2, ARCANE_EXPLOSION)
						.effectStacked(ELECTRICAL_CHARGE, ITEM, player, 3)
						.effectRemoved(ELECTRICAL_CHARGE, ITEM, player)
						.cooldownStarted(player, cooldownId, 2.5)
						.decreasedResource(750, HEALTH, target2, LIGHTNING_BOLT)
						.decreasedResource(392, HEALTH, false, target3, ARCANE_EXPLOSION)
						.decreasedResource(392, HEALTH, false, target4, ARCANE_EXPLOSION)
						.decreasedResource(392, HEALTH, false, target5, ARCANE_EXPLOSION),
				at(5.5)
						.cooldownExpired(player, cooldownId)
		);
	}

	CooldownId cooldownId = CooldownId.of(SpellId.of(210128785));

	@Override
	protected void afterSetUp() {
		equip("The Lightning Capacitor", TRINKET_1);
	}
}

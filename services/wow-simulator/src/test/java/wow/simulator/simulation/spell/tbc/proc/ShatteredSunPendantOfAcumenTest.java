package wow.simulator.simulation.spell.tbc.proc;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.SpellId;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ITEM;
import static wow.test.commons.AbilityNames.SEARING_PAIN;

/**
 * User: POlszewski
 * Date: 11.08.2026
 */
class ShatteredSunPendantOfAcumenTest extends TbcWarlockSpellSimulationTest {
	/*
	Equip: Your spells have a chance to call on the power of the Arcane if you're exalted with the Scryers, or the Light if you're exalted with the Aldor. (Proc chance: 15%, 45s cooldown)
	 */

	@Test
	void scryer_proc_is_triggered_only_once() {
		eventsOnlyOnFollowingRolls(0, 1);

		player.getExclusiveFactions().enable("The Scryers");

		player.cast(SEARING_PAIN);
		player.cast(SEARING_PAIN);

		updateUntil(60);

		assertEvents(
			at(0)
					.beginCast(player, SEARING_PAIN, 1.5)
					.beginGcd(player),
			at(1.5)
					.endCast(player, SEARING_PAIN)
					.decreasedResource(205, MANA, player, SEARING_PAIN)
					.cooldownStarted(player, scryersCooldownId, 45)
					.decreasedResource(350, HEALTH, target, "Arcane Bolt")
					.decreasedResource(310, HEALTH, target, SEARING_PAIN)
					.endGcd(player)
					.beginCast(player, SEARING_PAIN, 1.5)
					.beginGcd(player),
			at(3)
					.endCast(player, SEARING_PAIN)
					.decreasedResource(205, MANA, player, SEARING_PAIN)
					.decreasedResource(310, HEALTH, target, SEARING_PAIN)
					.endGcd(player),
			at(46.5)
					.cooldownExpired(player, scryersCooldownId)
		);
	}

	@Test
	void aldor_proc_is_triggered_only_once() {
		eventsOnlyOnFollowingRolls(0, 1);

		player.getExclusiveFactions().enable("The Aldor");

		player.cast(SEARING_PAIN);
		player.cast(SEARING_PAIN);

		updateUntil(60);

		assertEvents(
				at(0)
						.beginCast(player, SEARING_PAIN, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, SEARING_PAIN)
						.decreasedResource(205, MANA, player, SEARING_PAIN)
						.cooldownStarted(player, aldorCooldownId, 45)
						.effectApplied("Light's Wrath", ITEM, player, 10)
						.decreasedResource(362, HEALTH, target, SEARING_PAIN)
						.endGcd(player)
						.beginCast(player, SEARING_PAIN, 1.5)
						.beginGcd(player),
				at(3)
						.endCast(player, SEARING_PAIN)
						.decreasedResource(205, MANA, player, SEARING_PAIN)
						.decreasedResource(362, HEALTH, target, SEARING_PAIN)
						.endGcd(player),
				at(11.5)
						.effectExpired("Light's Wrath", ITEM, player),
				at(46.5)
						.cooldownExpired(player, aldorCooldownId)
		);
	}

	CooldownId scryersCooldownId = CooldownId.of(SpellId.of(45429));
	CooldownId aldorCooldownId = CooldownId.of(SpellId.of(45479));

	@Override
	protected void afterSetUp() {
		equip("Shattered Sun Pendant of Acumen");
	}
}

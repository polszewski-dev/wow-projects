package wow.simulator.simulation.spell.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.AbilityId.ZANDALARIAN_HERO_CHARM;
import static wow.commons.model.spell.GroupCooldownId.TRINKET;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.simulator.WowSimulatorSpringTest.EventCollectingHandler.Event;

/**
 * User: POlszewski
 * Date: 2024-12-02
 */
class ZandalarianHeroCharmTest extends SpellSimulationTest {
	/*
	Use: Increases your spell damage by up to 204 and your healing by up to 408 for 20 sec.
	Every time you cast a spell, the bonus is reduced by 17 spell damage and 34 healing. (2 Min Cooldown)
	 */
	@Test
	void effectIsApplied() {
		player.cast(ZANDALARIAN_HERO_CHARM);

		simulation.updateUntil(Time.at(150));

		assertEvents(
				at(0)
						.beginCast(player, ZANDALARIAN_HERO_CHARM)
						.endCast(player, ZANDALARIAN_HERO_CHARM)
						.cooldownStarted(player, ZANDALARIAN_HERO_CHARM)
						.cooldownStarted(player, TRINKET)
						.effectApplied(ZANDALARIAN_HERO_CHARM, player),
				at(20)
						.cooldownExpired(player, TRINKET)
						.effectExpired(ZANDALARIAN_HERO_CHARM, player),
				at(120)
						.cooldownExpired(player, ZANDALARIAN_HERO_CHARM)
		);
	}

	@Test
	void stacksDecrease() {
		rng.eventRoll = true;

		player.cast(ZANDALARIAN_HERO_CHARM);

		for (int i = 0; i < 10; ++i) {
			player.cast(SHADOW_BOLT);
		}

		simulation.updateUntil(Time.at(30));

		assertEvents(
				Event::isEffect,
				at(0)
						.effectApplied(ZANDALARIAN_HERO_CHARM, player),
				at(3)
						.effectStacksDecreased(ZANDALARIAN_HERO_CHARM, player, 11),
				at(6)
						.effectStacksDecreased(ZANDALARIAN_HERO_CHARM, player, 10),
				at(9)
						.effectStacksDecreased(ZANDALARIAN_HERO_CHARM, player, 9),
				at(12)
						.effectStacksDecreased(ZANDALARIAN_HERO_CHARM, player, 8),
				at(15)
						.effectStacksDecreased(ZANDALARIAN_HERO_CHARM, player, 7),
				at(18)
						.effectStacksDecreased(ZANDALARIAN_HERO_CHARM, player, 6),
				at(20)
						.effectExpired(ZANDALARIAN_HERO_CHARM, player)
		);
	}

	@Test
	void damageDecreases() {
		rng.eventRoll = true;

		player.cast(ZANDALARIAN_HERO_CHARM);

		for (int i = 0; i < 10; ++i) {
			player.cast(SHADOW_BOLT);
		}

		simulation.updateUntil(Time.at(30));

		assertEvents(
				Event::isDamage,
				at(3)
						.decreasedResource(735, HEALTH, target, SHADOW_BOLT),
				at(6)
						.decreasedResource(721, HEALTH, target, SHADOW_BOLT),
				at(9)
						.decreasedResource(706, HEALTH, target, SHADOW_BOLT),
				at(12)
						.decreasedResource(692, HEALTH, target, SHADOW_BOLT),
				at(15)
						.decreasedResource(677, HEALTH, target, SHADOW_BOLT),
				at(18)
						.decreasedResource(662, HEALTH, target, SHADOW_BOLT),
				at(21)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT),
				at(24)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT),
				at(27)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT),
				at(30)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
		);
	}

	@Override
	protected void afterSetUp() {
		equip(ZANDALARIAN_HERO_CHARM.getName(), TRINKET_1);
	}
}

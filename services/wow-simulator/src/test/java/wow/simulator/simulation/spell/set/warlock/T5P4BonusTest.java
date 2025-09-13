package wow.simulator.simulation.spell.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;
import wow.simulator.util.TestEvent;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T5P4BonusTest extends WarlockSpellSimulationTest {
	/*
	Your Shadowbolt spell hits increase the damage of Corruption by 10% and your Incinerate spell hits increase the damage of Immolate by 10%.
	 */

	@Test
	void oneSBCast() {
		player.cast(CORRUPTION);
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				TestEvent::isDamage,
				at(3)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(4.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(6)
						.decreasedResource(200, HEALTH, target, CORRUPTION),
				at(9)
						.decreasedResource(200, HEALTH, target, CORRUPTION),
				at(12)
						.decreasedResource(201, HEALTH, target, CORRUPTION),
				at(15)
						.decreasedResource(200, HEALTH, target, CORRUPTION),
				at(18)
						.decreasedResource(200, HEALTH, target, CORRUPTION)
		);
	}

	@Test
	void multipleSBCasts() {
		player.cast(CORRUPTION);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				TestEvent::isDamage,
				at(3)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(4.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(6)
						.decreasedResource(200, HEALTH, target, CORRUPTION),
				at(7.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(9)
						.decreasedResource(219, HEALTH, target, CORRUPTION),
				at(10.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(12)
						.decreasedResource(236, HEALTH, target, CORRUPTION),
				at(13.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(15)
						.decreasedResource(255, HEALTH, target, CORRUPTION),
				at(16.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(18)
						.decreasedResource(274, HEALTH, target, CORRUPTION)
		);
	}

	@Test
	void secondCorruptionIsNotAffectedByPreviousSBCasts() {
		player.cast(CORRUPTION);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(CORRUPTION);

		updateUntil(60);

		assertEvents(
				TestEvent::isDamage,
				at(3)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(4.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(6)
						.decreasedResource(200, HEALTH, target, CORRUPTION),
				at(7.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(9)
						.decreasedResource(219, HEALTH, target, CORRUPTION),
				at(10.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(12)
						.decreasedResource(236, HEALTH, target, CORRUPTION),
				at(13.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(15)
						.decreasedResource(255, HEALTH, target, CORRUPTION),
				at(16.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(18)
						.decreasedResource(274, HEALTH, target, CORRUPTION),
				at(19.5)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(22.5)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(25.5)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(28.5)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(31.5)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(34.5)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(37.5)
						.decreasedResource(182, HEALTH, target, CORRUPTION)
		);
	}

	@Test
	void nothingHappensWithoutCorruption() {
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				TestEvent::isDamage,
				at(3)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT)
		);
	}

	@Test
	void oneIncinerateCast() {
		player.cast(IMMOLATE);
		player.idleUntil(Time.at(5));
		player.cast(INCINERATE);

		updateUntil(30);

		assertEvents(
				TestEvent::isDamage,
				at(2)
						.decreasedResource(370, HEALTH, target, IMMOLATE),
				at(5)
						.decreasedResource(149, HEALTH, target, IMMOLATE),
				at(7.5)
						.decreasedResource(745, HEALTH, target, INCINERATE),
				at(8)
						.decreasedResource(164, HEALTH, target, IMMOLATE),
				at(11)
						.decreasedResource(164, HEALTH, target, IMMOLATE),
				at(14)
						.decreasedResource(164, HEALTH, target, IMMOLATE),
				at(17)
						.decreasedResource(164, HEALTH, target, IMMOLATE)
		);
	}

	@Test
	void multipleIncinerateCasts() {
		player.cast(IMMOLATE);
		player.idleUntil(Time.at(5));
		player.cast(INCINERATE);
		player.cast(INCINERATE);
		player.cast(INCINERATE);
		player.cast(INCINERATE);

		updateUntil(30);

		assertEvents(
				TestEvent::isDamage,
				at(2)
						.decreasedResource(370, HEALTH, target, IMMOLATE),
				at(5)
						.decreasedResource(149, HEALTH, target, IMMOLATE),
				at(7.5)
						.decreasedResource(745, HEALTH, target, INCINERATE),
				at(8)
						.decreasedResource(164, HEALTH, target, IMMOLATE),
				at(10)
						.decreasedResource(745, HEALTH, target, INCINERATE),
				at(11)
						.decreasedResource(179, HEALTH, target, IMMOLATE),
				at(12.5)
						.decreasedResource(745, HEALTH, target, INCINERATE),
				at(14)
						.decreasedResource(194, HEALTH, target, IMMOLATE),
				at(15)
						.decreasedResource(745, HEALTH, target, INCINERATE),
				at(17)
						.decreasedResource(209, HEALTH, target, IMMOLATE)
		);
	}

	@Test
	void secondImmolateIsNotAffectedByPreviousIncinerateCasts() {
		player.cast(IMMOLATE);
		player.idleUntil(Time.at(5));
		player.cast(INCINERATE);
		player.cast(INCINERATE);
		player.cast(INCINERATE);
		player.cast(INCINERATE);
		player.cast(IMMOLATE);

		updateUntil(60);

		assertEvents(
				TestEvent::isDamage,
				at(2)
						.decreasedResource(370, HEALTH, target, IMMOLATE),
				at(5)
						.decreasedResource(149, HEALTH, target, IMMOLATE),
				at(7.5)
						.decreasedResource(745, HEALTH, target, INCINERATE),
				at(8)
						.decreasedResource(164, HEALTH, target, IMMOLATE),
				at(10)
						.decreasedResource(745, HEALTH, target, INCINERATE),
				at(11)
						.decreasedResource(179, HEALTH, target, IMMOLATE),
				at(12.5)
						.decreasedResource(745, HEALTH, target, INCINERATE),
				at(14)
						.decreasedResource(194, HEALTH, target, IMMOLATE),
				at(15)
						.decreasedResource(745, HEALTH, target, INCINERATE),
				at(17)
						.decreasedResource(209, HEALTH, target, IMMOLATE)
						.decreasedResource(370, HEALTH, target, IMMOLATE),
				at(20)
						.decreasedResource(149, HEALTH, target, IMMOLATE),
				at(23)
						.decreasedResource(149, HEALTH, target, IMMOLATE),
				at(26)
						.decreasedResource(149, HEALTH, target, IMMOLATE),
				at(29)
						.decreasedResource(149, HEALTH, target, IMMOLATE),
				at(32)
						.decreasedResource(150, HEALTH, target, IMMOLATE)

		);
	}

	@Test
	void nothingHappensWithoutImmolate() {
		player.cast(INCINERATE);

		updateUntil(30);

		assertEvents(
				TestEvent::isDamage,
				at(2.5)
						.decreasedResource(626, HEALTH, target, INCINERATE)
		);
	}

	@Test
	void incinerateDoesNotAffectCorruption() {
		player.cast(CORRUPTION);
		player.cast(INCINERATE);

		updateUntil(30);

		assertEvents(
				TestEvent::isDamage,
				at(3)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(4)
						.decreasedResource(626, HEALTH, target, INCINERATE),
				at(6)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(9)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(12)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(15)
						.decreasedResource(182, HEALTH, target, CORRUPTION),
				at(18)
						.decreasedResource(182, HEALTH, target, CORRUPTION)
		);
	}

	@Test
	void shadowBoltDoesNotAffectImmolate() {
		player.cast(IMMOLATE);
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				TestEvent::isDamage,
				at(2)
						.decreasedResource(370, HEALTH, target, IMMOLATE),
				at(5)
						.decreasedResource(149, HEALTH, target, IMMOLATE)
						.decreasedResource(752, HEALTH, target, SHADOW_BOLT),
				at(8)
						.decreasedResource(149, HEALTH, target, IMMOLATE),
				at(11)
						.decreasedResource(149, HEALTH, target, IMMOLATE),
				at(14)
						.decreasedResource(149, HEALTH, target, IMMOLATE),
				at(17)
						.decreasedResource(150, HEALTH, target, IMMOLATE)
		);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(IMPROVED_CORRUPTION, 5);

		equip("Hood of the Corruptor");
		equip("Mantle of the Corruptor");
		equip("Robe of the Corruptor");
		equip("Leggings of the Corruptor");
	}
}

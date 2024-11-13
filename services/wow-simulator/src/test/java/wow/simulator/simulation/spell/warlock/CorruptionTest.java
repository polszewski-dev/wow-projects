package wow.simulator.simulation.spell.warlock;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wow.commons.model.buff.BuffId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.EMPOWERED_CORRUPTION;
import static wow.commons.model.talent.TalentId.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class CorruptionTest extends SpellSimulationTest {
	@Test
	void success() {
		player.cast(CORRUPTION);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target),
				at(5)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(8)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(11)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(14)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(17)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(20)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}

	@Test
	void resisted() {
		rng.hitRoll = false;

		player.cast(CORRUPTION);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.spellResisted(player, CORRUPTION, target)
		);
	}

	@Test
	void interrupted() {
		enableTalent(IMPROVED_CORRUPTION, 2);

		player.cast(CORRUPTION);

		simulation.updateUntil(Time.at(1));

		player.interruptCurrentAction();

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION)
						.beginGcd(player),
				at(1)
						.castInterrupted(player, CORRUPTION)
						.endGcd(player)
		);
	}

	@Nested
	class SpecialCases {
		@Test
		void improvedCorruption() {
			enableTalent(IMPROVED_CORRUPTION, 5);

			player.cast(CORRUPTION);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CORRUPTION)
							.endCast(player, CORRUPTION)
							.decreasedResource(370, MANA, player, CORRUPTION)
							.effectApplied(CORRUPTION, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(3)
							.decreasedResource(150, HEALTH, target, CORRUPTION),
					at(6)
							.decreasedResource(150, HEALTH, target, CORRUPTION),
					at(9)
							.decreasedResource(150, HEALTH, target, CORRUPTION),
					at(12)
							.decreasedResource(150, HEALTH, target, CORRUPTION),
					at(15)
							.decreasedResource(150, HEALTH, target, CORRUPTION),
					at(18)
							.decreasedResource(150, HEALTH, target, CORRUPTION)
							.effectExpired(CORRUPTION, target)
			);
		}

		@Test
		void empoweredCorruption() {
			enableTalent(IMPROVED_CORRUPTION, 5);
			enableTalent(EMPOWERED_CORRUPTION, 3);
			enableBuff(BuffId.FEL_ARMOR, 2);

			player.cast(CORRUPTION);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CORRUPTION)
							.endCast(player, CORRUPTION)
							.decreasedResource(370, MANA, player, CORRUPTION)
							.effectApplied(CORRUPTION, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(3)
							.decreasedResource(171, HEALTH, target, CORRUPTION),
					at(6)
							.decreasedResource(172, HEALTH, target, CORRUPTION),
					at(9)
							.decreasedResource(171, HEALTH, target, CORRUPTION),
					at(12)
							.decreasedResource(172, HEALTH, target, CORRUPTION),
					at(15)
							.decreasedResource(171, HEALTH, target, CORRUPTION),
					at(18)
							.decreasedResource(172, HEALTH, target, CORRUPTION)
							.effectExpired(CORRUPTION, target)
			);
		}

		@Test
		void t5Bonus() {
			enableTalent(IMPROVED_CORRUPTION, 5);

			equip("Voidheart Crown");
			equip("Voidheart Gloves");
			equip("Voidheart Leggings");
			equip("Voidheart Mantle");

			player.cast(CORRUPTION);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CORRUPTION)
							.endCast(player, CORRUPTION)
							.decreasedResource(370, MANA, player, CORRUPTION)
							.effectApplied(CORRUPTION, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(3)
							.decreasedResource(175, HEALTH, target, CORRUPTION),
					at(6)
							.decreasedResource(175, HEALTH, target, CORRUPTION),
					at(9)
							.decreasedResource(175, HEALTH, target, CORRUPTION),
					at(12)
							.decreasedResource(175, HEALTH, target, CORRUPTION),
					at(15)
							.decreasedResource(175, HEALTH, target, CORRUPTION),
					at(18)
							.decreasedResource(175, HEALTH, target, CORRUPTION),
					at(21)
							.decreasedResource(175, HEALTH, target, CORRUPTION)
							.effectExpired(CORRUPTION, target)
			);
		}
	}
}

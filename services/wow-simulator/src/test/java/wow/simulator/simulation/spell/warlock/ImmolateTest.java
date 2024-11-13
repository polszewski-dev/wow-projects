package wow.simulator.simulation.spell.warlock;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.spell.AbilityId.IMMOLATE;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.BANE;
import static wow.commons.model.talent.TalentId.IMPROVED_IMMOLATE;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class ImmolateTest extends SpellSimulationTest {
	@Test
	void success() {
		player.cast(IMMOLATE);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, IMMOLATE)
						.decreasedResource(445, MANA, player, IMMOLATE)
						.decreasedResource(332, HEALTH, target, IMMOLATE)
						.effectApplied(IMMOLATE, target),
				at(5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(8)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(11)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(14)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(17)
						.decreasedResource(123, HEALTH, target, IMMOLATE)
						.effectExpired(IMMOLATE, target)
		);
	}

	@Test
	void resisted() {
		rng.hitRoll = false;

		player.cast(IMMOLATE);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, IMMOLATE)
						.decreasedResource(445, MANA, player, IMMOLATE)
						.spellResisted(player, IMMOLATE, target)
		);
	}

	@Test
	void interrupted() {
		player.cast(IMMOLATE);

		simulation.updateUntil(Time.at(1));

		player.interruptCurrentAction();

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE)
						.beginGcd(player),
				at(1)
						.castInterrupted(player, IMMOLATE)
						.endGcd(player)
		);
	}

	@Nested
	class SpecialCases {
		@Test
		void bane() {
			enableTalent(BANE, 5);

			player.cast(IMMOLATE);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, IMMOLATE)
							.beginGcd(player),
					at(1.5)
							.endCast(player, IMMOLATE)
							.decreasedResource(445, MANA, player, IMMOLATE)
							.decreasedResource(332, HEALTH, target, IMMOLATE)
							.effectApplied(IMMOLATE, target)
							.endGcd(player),
					at(4.5)
							.decreasedResource(123, HEALTH, target, IMMOLATE),
					at(7.5)
							.decreasedResource(123, HEALTH, target, IMMOLATE),
					at(10.5)
							.decreasedResource(123, HEALTH, target, IMMOLATE),
					at(13.5)
							.decreasedResource(123, HEALTH, target, IMMOLATE),
					at(16.5)
							.decreasedResource(123, HEALTH, target, IMMOLATE)
							.effectExpired(IMMOLATE, target)
			);
		}

		@Test
		void improvedImmolate() {
			enableTalent(IMPROVED_IMMOLATE, 5);

			player.cast(IMMOLATE);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, IMMOLATE)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(2)
							.endCast(player, IMMOLATE)
							.decreasedResource(445, MANA, player, IMMOLATE)
							.decreasedResource(415, HEALTH, target, IMMOLATE)
							.effectApplied(IMMOLATE, target),
					at(5)
							.decreasedResource(123, HEALTH, target, IMMOLATE),
					at(8)
							.decreasedResource(123, HEALTH, target, IMMOLATE),
					at(11)
							.decreasedResource(123, HEALTH, target, IMMOLATE),
					at(14)
							.decreasedResource(123, HEALTH, target, IMMOLATE),
					at(17)
							.decreasedResource(123, HEALTH, target, IMMOLATE)
							.effectExpired(IMMOLATE, target)
			);
		}
	}
}

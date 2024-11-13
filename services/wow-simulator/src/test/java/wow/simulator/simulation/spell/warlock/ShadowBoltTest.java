package wow.simulator.simulation.spell.warlock;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.BANE;
import static wow.commons.model.talent.TalentId.CATACLYSM;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class ShadowBoltTest extends SpellSimulationTest {
	@Test
	void success() {
		player.cast(SHADOW_BOLT);

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
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
		);
	}

	@Test
	void resisted() {
		rng.hitRoll = false;

		player.cast(SHADOW_BOLT);

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
						.spellResisted(player, SHADOW_BOLT, target)
		);
	}

	@Test
	void interrupted() {
		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(1));

		player.interruptCurrentAction();

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(1)
						.castInterrupted(player, SHADOW_BOLT)
						.endGcd(player)
		);
	}

	@Nested
	class SpecialCases {
		@Test
		void bane() {
			enableTalent(BANE, 5);

			player.cast(SHADOW_BOLT);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SHADOW_BOLT)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(2.5)
							.endCast(player, SHADOW_BOLT)
							.decreasedResource(420, MANA, player, SHADOW_BOLT)
							.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
			);
		}

		@Test
		void cataclysm() {
			enableTalent(CATACLYSM, 5);

			player.cast(SHADOW_BOLT);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SHADOW_BOLT)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(3)
							.endCast(player, SHADOW_BOLT)
							.decreasedResource(399, MANA, player, SHADOW_BOLT)
							.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
			);
		}
	}
}

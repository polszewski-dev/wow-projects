package wow.simulator.simulation.spell.warlock;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.LIFE_TAP;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.IMPROVED_LIFE_TAP;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class LifeTapTest extends SpellSimulationTest {
	@Test
	void success() {
		setMana(player, 0);

		player.cast(LIFE_TAP);

		simulation.updateUntil(Time.at(30));

		assertThat(player.getCurrentMana()).isEqualTo(582);

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP)
						.endCast(player, LIFE_TAP)
						.decreasedResource(582, HEALTH, player, LIFE_TAP)
						.increasedResource(582, MANA, player, LIFE_TAP)
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void resisted() {
		rng.hitRoll = false;

		setMana(player, 0);

		player.cast(LIFE_TAP);

		simulation.updateUntil(Time.at(30));

		assertThat(player.getCurrentMana()).isEqualTo(582);

		//can't resist friendly spell

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP)
						.endCast(player, LIFE_TAP)
						.decreasedResource(582, HEALTH, player, LIFE_TAP)
						.increasedResource(582, MANA, player, LIFE_TAP)
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void interrupted() {
		setMana(player, 0);

		player.cast(LIFE_TAP);

		simulation.updateUntil(Time.at(1.25));

		player.interruptCurrentAction();

		simulation.updateUntil(Time.at(30));

		assertThat(player.getCurrentMana()).isEqualTo(582);

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP)
						.endCast(player, LIFE_TAP)
						.decreasedResource(582, HEALTH, player, LIFE_TAP)
						.increasedResource(582, MANA, player, LIFE_TAP)
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
		);
	}

	@Nested
	class SpecialCases {
		@Test
		void improvedLifeTap() {
			enableTalent(IMPROVED_LIFE_TAP, 2);
			setMana(player, 0);

			player.cast(LIFE_TAP);

			simulation.updateUntil(Time.at(30));

			assertThat(player.getCurrentMana()).isEqualTo(698);

			assertEvents(
					at(0)
							.beginCast(player, LIFE_TAP)
							.endCast(player, LIFE_TAP)
							.decreasedResource(698, HEALTH, player, LIFE_TAP)
							.increasedResource(698, MANA, player, LIFE_TAP)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Test
		void improvedLifeTapAndSp() {
			enableTalent(IMPROVED_LIFE_TAP, 2);
			setMana(player, 0);

			equip("Tempest of Chaos");

			player.cast(LIFE_TAP);

			simulation.updateUntil(Time.at(30));

			assertThat(player.getCurrentMana()).isEqualTo(947);// (582 + 0.8 * 259) * 1.2

			assertEvents(
					at(0)
							.beginCast(player, LIFE_TAP)
							.endCast(player, LIFE_TAP)
							.decreasedResource(947, HEALTH, player, LIFE_TAP)
							.increasedResource(947, MANA, player, LIFE_TAP)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Test
		void t3Bonus() {
			enableTalent(IMPROVED_LIFE_TAP, 2);
			setMana(player, 0);

			equip("Plagueheart Belt");
			equip("Plagueheart Bindings");
			equip("Plagueheart Circlet");
			equip("Plagueheart Gloves");
			equip("Plagueheart Leggings");
			equip("Plagueheart Robe");
			equip("Plagueheart Sandals");
			equip("Plagueheart Shoulderpads");

			player.cast(LIFE_TAP);

			simulation.updateUntil(Time.at(30));

			assertThat(player.getCurrentMana()).isEqualTo(959);// (582 + 0.8*272) * 1.2

			assertEvents(
					at(0)
							.beginCast(player, LIFE_TAP)
							.endCast(player, LIFE_TAP)
							.decreasedResource(843, HEALTH, player, LIFE_TAP)
							.increasedResource(959, MANA, player, LIFE_TAP)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}
	}
}

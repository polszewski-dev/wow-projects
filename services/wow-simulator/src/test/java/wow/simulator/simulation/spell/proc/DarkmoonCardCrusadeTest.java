package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.WowSimulatorSpringTest.EventCollectingHandler.Event;

/**
 * User: POlszewski
 * Date: 2024-12-02
 */
class DarkmoonCardCrusadeTest extends SpellSimulationTest {
	/*
	Equip: Each time you deal melee or ranged damage to an opponent, you gain 6 attack power for the next 10 sec., stacking up to 20 times.
  	Each time you land a harmful spell on an opponent, you gain 8 spell damage for the next 10 sec., stacking up to 10 times.
	 */
	@Test
	void procIsTriggered() {
		rng.eventRoll = true;

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
						.decreasedResource(575, HEALTH, false, target, SHADOW_BOLT)
						.effectApplied("Darkmoon Card: Crusade", player),
				at(13)
						.effectExpired("Darkmoon Card: Crusade", player)
		);
	}

	@Test
	void effectIsStacked() {
		rng.eventRoll = true;

		for (int i = 0; i < 11; ++i) {
			player.cast(SHADOW_BOLT);
		}

		simulation.updateUntil(Time.at(60));

		assertEvents(
				Event::isEffect,
				at(3)
						.effectApplied("Darkmoon Card: Crusade", player),
				at(6)
						.effectStacked("Darkmoon Card: Crusade", player, 2),
				at(9)
						.effectStacked("Darkmoon Card: Crusade", player, 3),
				at(12)
						.effectStacked("Darkmoon Card: Crusade", player, 4),
				at(15)
						.effectStacked("Darkmoon Card: Crusade", player, 5),
				at(18)
						.effectStacked("Darkmoon Card: Crusade", player, 6),
				at(21)
						.effectStacked("Darkmoon Card: Crusade", player, 7),
				at(24)
						.effectStacked("Darkmoon Card: Crusade", player, 8),
				at(27)
						.effectStacked("Darkmoon Card: Crusade", player, 9),
				at(30)
						.effectStacked("Darkmoon Card: Crusade", player, 10),
				at(33)
						.effectStacked("Darkmoon Card: Crusade", player, 10),
				at(43)
						.effectExpired("Darkmoon Card: Crusade", player)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = {
			1, 2, 3, 4, 5, 6, 7, 8, 9, 10
	})
	void modifierIsTakenIntoAccount(int numCasts) {
		var dmgBefore = player.getStats().getSpellDamage();

		rng.eventRoll = true;

		for (int i = 0; i < numCasts; ++i) {
			player.cast(SHADOW_BOLT);
		}

		simulation.updateUntil(Time.at(numCasts * 3 + 1));

		var dmgAfter = player.getStats().getSpellDamage();

		assertThat(dmgAfter).isEqualTo(dmgBefore + numCasts * 8);
	}

	@Override
	protected void afterSetUp() {
		equip("Darkmoon Card: Crusade", TRINKET_1);
	}
}

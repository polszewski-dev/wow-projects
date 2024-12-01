package wow.simulator.simulation.spell.ability.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.LIFE_TAP;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

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
}

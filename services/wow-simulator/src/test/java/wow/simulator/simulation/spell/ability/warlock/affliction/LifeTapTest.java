package wow.simulator.simulation.spell.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.LIFE_TAP;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class LifeTapTest extends WarlockSpellSimulationTest {
	@Test
	void success() {
		setMana(player, 0);

		player.cast(LIFE_TAP);

		updateUntil(30);

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
		missesOnlyOnFollowingRolls(0);

		setMana(player, 0);

		player.cast(LIFE_TAP);

		updateUntil(30);

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

		updateUntil(30);

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

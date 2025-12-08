package wow.simulator.simulation.spell.tbc.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.LIFE_TAP;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class LifeTapTest extends TbcWarlockSpellSimulationTest {
	/*
	Converts 582 health into 582 mana.
	 */

	@Test
	void success() {
		setMana(player, 0);

		player.cast(LIFE_TAP);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP)
						.beginGcd(player)
						.endCast(player, LIFE_TAP)
						.decreasedResource(582, HEALTH, player, LIFE_TAP)
						.increasedResource(582, MANA, player, LIFE_TAP),
				at(1.5)
						.endGcd(player)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void correct_amount_of_mana_gained(int spellPower) {
		addSpBonus(spellPower);
		setMana(player, 0);

		player.cast(LIFE_TAP);

		updateUntil(30);

		var actual = player.getCurrentMana() - regeneratedMana;
		var expected = (int) LIFE_TAP_INFO.damage(spellPower);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void resisted() {
		missesOnlyOnFollowingRolls(0);

		setMana(player, 0);

		player.cast(LIFE_TAP);

		updateUntil(30);

		assertThat(player.getCurrentMana() - regeneratedMana).isEqualTo(582);

		//can't resist friendly spell

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP)
						.beginGcd(player)
						.endCast(player, LIFE_TAP)
						.decreasedResource(582, HEALTH, player, LIFE_TAP)
						.increasedResource(582, MANA, player, LIFE_TAP),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void interrupted() {
		setMana(player, 0);

		player.cast(LIFE_TAP);

		runAt(1.25, player::interruptCurrentAction);

		updateUntil(30);

		assertThat(player.getCurrentMana() - regeneratedMana).isEqualTo(582);

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP)
						.beginGcd(player)
						.endCast(player, LIFE_TAP)
						.decreasedResource(582, HEALTH, player, LIFE_TAP)
						.increasedResource(582, MANA, player, LIFE_TAP),
				at(1.5)
						.endGcd(player)
		);
	}
}

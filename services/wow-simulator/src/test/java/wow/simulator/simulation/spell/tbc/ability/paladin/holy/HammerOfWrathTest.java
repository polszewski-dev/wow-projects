package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.HAMMER_OF_WRATH;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class HammerOfWrathTest extends TbcPaladinSpellSimulationTest {
	/*
	Hurls a hammer that strikes an enemy for 672 to 742 Holy damage. Only usable on enemies that have 20% or less health.
	 */

	@Test
	void success() {
		setHealthPct(target, 20);

		player.cast(HAMMER_OF_WRATH);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, HAMMER_OF_WRATH, 0.5)
						.beginGcd(player),
				at(0.5)
						.endCast(player, HAMMER_OF_WRATH)
						.decreasedResource(440, MANA, player, HAMMER_OF_WRATH)
						.cooldownStarted(player, HAMMER_OF_WRATH, 6)
						.decreasedResource(707, HEALTH, target, HAMMER_OF_WRATH),
				at(1.5)
						.endGcd(player),
				at(6.5)
						.cooldownExpired(player, HAMMER_OF_WRATH)
		);
	}

	@ParameterizedTest
	@CsvSource({
			"19, true",
			"20, true",
			"21, false",
	})
	void can_only_be_casted_when_targets_health_is_at_most_20_pct(int pct, boolean expected) {
		setHealthPct(target, pct);

		var actual = player.canCast(HAMMER_OF_WRATH);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		setHealthPct(target, 20);

		simulateDamagingSpell(HAMMER_OF_WRATH, spellDamage);

		assertDamageDone(HAMMER_OF_WRATH_INFO, spellDamage);
	}
}

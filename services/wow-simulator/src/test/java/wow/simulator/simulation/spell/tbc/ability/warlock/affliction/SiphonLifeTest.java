package wow.simulator.simulation.spell.tbc.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SIPHON_LIFE;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class SiphonLifeTest extends TbcWarlockSpellSimulationTest {
	/*
	Transfers 63 health from the target to the caster every 3 sec.  Lasts 30 sec.
	 */

	@Test
	void success() {
		player.cast(SIPHON_LIFE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SIPHON_LIFE)
						.beginGcd(player)
						.endCast(player, SIPHON_LIFE)
						.decreasedResource(410, MANA, player, SIPHON_LIFE)
						.effectApplied(SIPHON_LIFE, target, 30),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(6)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(9)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(12)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(15)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(18)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(21)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(24)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(27)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(30)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE)
						.effectExpired(SIPHON_LIFE, target)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(SIPHON_LIFE, spellDamage);

		assertDamageDone(SIPHON_LIFE_INFO, spellDamage);
	}

	@Test
	void healthGained() {
		player.cast(SIPHON_LIFE);

		updateUntil(30);

		assertHealthGained(SIPHON_LIFE, player, SIPHON_LIFE_INFO.damage());
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.SIPHON_LIFE, 1);
		setHealth(player, 1000);
	}
}

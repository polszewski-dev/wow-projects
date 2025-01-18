package wow.simulator.simulation.spell.ability.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.CONFLAGRATE;
import static wow.commons.model.spell.AbilityId.IMMOLATE;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2025-01-18
 */
class ConflagrateTest extends WarlockSpellSimulationTest {
	/*
	Ignites a target that is already afflicted by your Immolate, dealing 579 to 721 Fire damage and consuming the Immolate spell.
	 */

	@Test
	void success() {
		player.cast(IMMOLATE);
		player.cast(CONFLAGRATE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, IMMOLATE)
						.decreasedResource(445, MANA, player, IMMOLATE)
						.decreasedResource(332, HEALTH, target, IMMOLATE)
						.effectApplied(IMMOLATE, target, 15)
						.beginCast(player, CONFLAGRATE)
						.endCast(player, CONFLAGRATE)
						.decreasedResource(305, MANA, player, CONFLAGRATE)
						.cooldownStarted(player, CONFLAGRATE, 10)
						.effectRemoved(IMMOLATE, target)
						.decreasedResource(650, HEALTH, target, CONFLAGRATE)
						.beginGcd(player),
				at(3.5)
						.endGcd(player),
				at(12)
						.cooldownExpired(player, CONFLAGRATE)
		);
	}

	@Test
	void canCastIfImmolatePresent() {
		player.cast(IMMOLATE);

		updateUntil(2);

		assertThat(player.canCast(CONFLAGRATE)).isTrue();
	}

	@Test
	void canNotCastIfImmolateIsMissing() {
		assertThat(player.canCast(CONFLAGRATE)).isFalse();
	}

	@Test
	void damageDone() {
		player.cast(IMMOLATE);
		player.cast(CONFLAGRATE);

		updateUntil(30);

		assertDamageDone(CONFLAGRATE, (579 + 721) / 2);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentId.CONFLAGRATE, 1);
	}
}

package wow.simulator.simulation.spell.ability.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.IMMOLATE;
import static wow.commons.model.spell.AbilityId.INCINERATE;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2025-01-18
 */
class IncinerateTest extends WarlockSpellSimulationTest {
	/*
	Deals 444 to 514 Fire damage to your target and an additional 111 to 128 Fire damage if the target is affected by an Immolate spell.
	 */

	@Test
	void success() {
		player.cast(INCINERATE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, INCINERATE, 2.5)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2.5)
						.endCast(player, INCINERATE)
						.decreasedResource(355, MANA, player, INCINERATE)
						.decreasedResource(479, HEALTH, target, INCINERATE)
		);
	}

	@Test
	void damageDone() {
		player.cast(INCINERATE);

		updateUntil(30);

		assertDamageDone(INCINERATE, (444 + 514) / 2);
	}

	@Test
	void additionalDamageWhenImmolateIsOnTarget() {
		player.cast(IMMOLATE);
		player.cast(INCINERATE);

		updateUntil(30);

		assertDamageDone(INCINERATE, (444 + 514) / 2 + (111 + 128) / 2);
	}
}

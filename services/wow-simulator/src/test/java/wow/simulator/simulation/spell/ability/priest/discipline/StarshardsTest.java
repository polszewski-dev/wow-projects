package wow.simulator.simulation.spell.ability.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.character.RaceId.NIGHT_ELF;
import static wow.commons.model.spell.AbilityId.STARSHARDS;
import static wow.commons.model.spell.ResourceType.HEALTH;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class StarshardsTest extends PriestSpellSimulationTest {
	/*
	Rains starshards down on the enemy target's head, causing 785 Arcane damage over 15 sec.
	 */

	@Test
	void success() {
		player.cast(STARSHARDS);

		updateUntil(30);

		assertEvents(
					 at(0)
							 .beginCast(player, STARSHARDS)
							 .beginGcd(player)
							 .endCast(player, STARSHARDS)
							 .cooldownStarted(player, STARSHARDS, 30)
							 .effectApplied(STARSHARDS, target, 15),
					 at(1.5)
							 .endGcd(player),
					 at(3)
							 .decreasedResource(157, HEALTH, target, STARSHARDS),
					 at(6)
							 .decreasedResource(157, HEALTH, target, STARSHARDS),
					 at(9)
							 .decreasedResource(157, HEALTH, target, STARSHARDS),
					 at(12)
							 .decreasedResource(157, HEALTH, target, STARSHARDS),
					 at(15)
							 .decreasedResource(157, HEALTH, target, STARSHARDS)
							 .effectExpired(STARSHARDS, target),
					 at(30)
							 .cooldownExpired(player, STARSHARDS)
		);
	}

	@Test
	void damageDone() {
		player.cast(STARSHARDS);

		updateUntil(30);

		assertDamageDone(STARSHARDS, STARSHARDS_INFO.damage());
	}

	@Override
	protected void beforeSetUp() {
		super.beforeSetUp();
		raceId = NIGHT_ELF;
	}
}

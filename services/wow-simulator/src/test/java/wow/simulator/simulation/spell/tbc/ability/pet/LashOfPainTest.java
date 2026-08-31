package wow.simulator.simulation.spell.tbc.ability.pet;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.character.PetType.SUCCUBUS;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.LASH_OF_PAIN;
import static wow.test.commons.AbilityNames.SUMMON_SUCCUBUS;

/**
 * User: POlszewski
 * Date: 2026-08-31
 */
class LashOfPainTest extends TbcWarlockSpellSimulationTest {
	/*
	An instant attack that lashes the target, causing 123 Shadow damage.
	 */
	@Test
	void success() {
		player.cast(SUMMON_SUCCUBUS);
		summonedPetCasts(player, LASH_OF_PAIN);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SUMMON_SUCCUBUS, 10)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(10)
						.endCast(player, SUMMON_SUCCUBUS)
						.decreasedResource(3428, MANA, player, SUMMON_SUCCUBUS)
						.petSummoned(player, SUCCUBUS)
						.beginCast(pet, LASH_OF_PAIN)
						.beginGcd(pet)
						.endCast(pet, LASH_OF_PAIN)
						.decreasedResource(190, MANA, pet, LASH_OF_PAIN)
						.cooldownStarted(pet, LASH_OF_PAIN, 12)
						.decreasedResource(123, HEALTH, target, LASH_OF_PAIN),
				at(11.5)
						.endGcd(pet),
				at(22)
						.cooldownExpired(pet, LASH_OF_PAIN)

		);
	}
}

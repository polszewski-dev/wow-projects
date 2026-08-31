package wow.simulator.simulation.spell.tbc.ability.pet;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.character.PetType.IMP;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FIREBOLT;
import static wow.test.commons.AbilityNames.SUMMON_IMP;

/**
 * User: POlszewski
 * Date: 2026-08-10
 */
class FireboltTest extends TbcWarlockSpellSimulationTest {
	/*
	Deals 112 to 127 Fire damage to a target.
	 */

	@Test
	void success() {
		player.cast(SUMMON_IMP);
		summonedPetCasts(player, FIREBOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SUMMON_IMP, 10)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(10)
						.endCast(player, SUMMON_IMP)
						.decreasedResource(2743, MANA, player, SUMMON_IMP)
						.petSummoned(player, IMP)
						.beginCast(pet, FIREBOLT, 2)
						.beginGcd(pet),
				at(11.5)
						.endGcd(pet),
				at(12)
						.endCast(pet, FIREBOLT)
						.decreasedResource(145, MANA, pet, FIREBOLT)
						.decreasedResource(119, HEALTH, target, FIREBOLT)
		);
	}
}

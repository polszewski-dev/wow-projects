package wow.simulator.simulation.spell.tbc.ability.pet;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.BLOOD_PACT;
import static wow.test.commons.AbilityNames.SUMMON_IMP;

/**
 * User: POlszewski
 * Date: 2026-08-10
 */
class BloodPactTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases party members' Stamina by 70.
	 */

	@Test
	void success() {
		player.cast(SUMMON_IMP);
		summonedPetCasts(player, BLOOD_PACT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SUMMON_IMP, 10)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(10)
						.endCast(player, SUMMON_IMP)
						.decreasedResource(2742, MANA, player, SUMMON_IMP)
						.beginCast(pet, BLOOD_PACT)
						.beginGcd(pet)
						.endCast(pet, BLOOD_PACT)
						.effectApplied(BLOOD_PACT, pet, Duration.INFINITE),
				at(11.5)
						.endGcd(pet)
		);
	}

	@Test
	void stamina_is_increased() {
		player.cast(SUMMON_IMP);
		summonedPetCasts(player, BLOOD_PACT);

		updateUntil(30);

		timeBefore = 0;
		timeAfter = 15;

		assertStaminaIncreasedBy(70);
	}
}

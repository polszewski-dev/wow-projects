package wow.simulator.simulation.spell.tbc.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.commons.model.character.PetType;
import wow.simulator.model.unit.Pet;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.DARK_PACT;

/**
 * User: POlszewski
 * Date: 2026-08-14
 */
class DarkPactTest extends TbcWarlockSpellSimulationTest {
	/*
	Drains 700 of your pet's Mana, returning 100% to you.
	 */

	@Test
	void success() {
		player.cast(DARK_PACT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, DARK_PACT)
						.beginGcd(player)
						.endCast(player, DARK_PACT)
						.decreasedResource(700, MANA, pet, DARK_PACT)
						.increasedResource(700, MANA, player, DARK_PACT),
				at(1.5)
						.endGcd(player)
		);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.DARK_PACT);

		player.summonPet(PetType.IMP, null);
		pet = player.getActivePet();

		setMana(player, 3000);
	}

	Pet pet;
}

package wow.simulator.simulation.spell.tbc.ability.warlock.demonology;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.PetType.SUCCUBUS;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SUMMON_SUCCUBUS;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class SummonSuccubusTest extends TbcWarlockSpellSimulationTest {
	/*
	Summons an Imp under the command of the Warlock.
	 */

	@Test
	void success() {
		player.cast(SUMMON_SUCCUBUS);

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
		);
	}

	@Test
	void pet_type_is_correct() {
		player.cast(SUMMON_SUCCUBUS);

		updateUntil(30);

		assertThat(player.getActivePetType()).isEqualTo(SUCCUBUS);
	}
}

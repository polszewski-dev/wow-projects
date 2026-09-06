package wow.simulator.simulation.spell.tbc.set.priest;

import org.junit.jupiter.api.Test;
import wow.commons.model.character.PetType;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;
import wow.simulator.util.TestEvent;

import static wow.test.commons.AbilityNames.SHADOWFIEND;

/**
 * User: POlszewski
 * Date: 2026-09-06
 */
class T4P2BonusTest extends TbcPriestSpellSimulationTest {
	/*
	Your Shadowfiend now has 75 more stamina and lasts 3 sec. longer.
	 */
	@Test
	void shadowfiend_duration_is_increased_by_3_sec() {
		player.cast(SHADOWFIEND);

		updateUntil(30);

		assertEvents(
				TestEvent::isPet,
				at(0)
						.petSummoned(player, PetType.SHADOWFIEND),
				atMillis(18_001)
						.petUnsummoned(player, PetType.SHADOWFIEND)
		);
	}

	@Override
	protected void afterSetUp() {
		equip("Gloves of the Incarnate");
		equip("Leggings of the Incarnate");
		equip("Shroud of the Incarnate");
		equip("Soul-Mantle of the Incarnate");
	}
}

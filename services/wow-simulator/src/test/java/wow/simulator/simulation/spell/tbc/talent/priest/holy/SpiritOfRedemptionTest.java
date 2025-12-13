package wow.simulator.simulation.spell.tbc.talent.priest.holy;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.TalentNames.SPIRIT_OF_REDEMPTION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SpiritOfRedemptionTest extends TbcPriestTalentSimulationTest {
	/*
	Increases total Spirit by 5% and upon death, the priest becomes the Spirit of Redemption for 15 sec.
	The Spirit of Redemption cannot move, attack, be attacked or targeted by any spells or effects.
	While in this form the priest can cast any healing spell free of cost.  When the effect ends, the priest dies.
	 */

	@Test
	void spirit_is_increased() {
		assertSpiritIsIncreasedByPct(SPIRIT_OF_REDEMPTION, 1, 5);
	}
}

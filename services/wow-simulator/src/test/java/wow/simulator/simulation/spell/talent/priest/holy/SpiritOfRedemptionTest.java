package wow.simulator.simulation.spell.talent.priest.holy;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.talent.TalentId.SPIRIT_OF_REDEMPTION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SpiritOfRedemptionTest extends PriestSpellSimulationTest {
	/*
	Increases total Spirit by 5% and upon death, the priest becomes the Spirit of Redemption for 15 sec.
	The Spirit of Redemption cannot move, attack, be attacked or targeted by any spells or effects.
	While in this form the priest can cast any healing spell free of cost.  When the effect ends, the priest dies.
	 */

	@Test
	void spiritIsIncreased() {
		var spiritBefore = player.getStats().getSpirit();

		enableTalent(SPIRIT_OF_REDEMPTION, 1);

		updateUntil(30);

		var spiritAfter = player.getStats().getSpirit();

		assertThat(spiritAfter).isEqualTo((int) (spiritBefore * 1.05));
	}
}

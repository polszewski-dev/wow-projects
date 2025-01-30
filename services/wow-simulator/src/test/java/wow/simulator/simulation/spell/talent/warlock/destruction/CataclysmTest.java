package wow.simulator.simulation.spell.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.talent.TalentId.CATACLYSM;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class CataclysmTest extends WarlockSpellSimulationTest {
	/*
	Reduces the Mana cost of your Destruction spells by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void cataclysm(int rank) {
		enableTalent(CATACLYSM, rank);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertManaPaid(SHADOW_BOLT, player, SHADOW_BOLT_INFO.manaCost(), -rank);
	}
}

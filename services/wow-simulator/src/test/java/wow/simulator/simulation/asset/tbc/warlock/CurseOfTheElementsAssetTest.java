package wow.simulator.simulation.asset.tbc.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.CURSE_OF_THE_ELEMENTS;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class CurseOfTheElementsAssetTest extends TbcAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, CURSE_OF_THE_ELEMENTS, target);
	}

	@Test
	void target_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(target, CURSE_OF_THE_ELEMENTS);
	}

	@Override
	protected void beforeSetUp() {
		setUpAfflictionWarlock(CURSE_OF_THE_ELEMENTS);
	}
}

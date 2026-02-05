package wow.simulator.simulation.asset.tbc.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.FEL_ARMOR;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class FelArmorAssetTest extends TbcAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, FEL_ARMOR, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, FEL_ARMOR);
	}

	@Override
	protected void beforeSetUp() {
		setUpDestroWarlock(FEL_ARMOR);
	}
}

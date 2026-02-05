package wow.simulator.simulation.asset.tbc.mage;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.MOLTEN_ARMOR;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class MoltenArmorAssetTest extends TbcAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, MOLTEN_ARMOR, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, MOLTEN_ARMOR);
	}

	@Override
	protected void beforeSetUp() {
		setUpFireMage(MOLTEN_ARMOR);
	}
}

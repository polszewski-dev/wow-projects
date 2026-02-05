package wow.simulator.simulation.asset.vanilla.mage;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.MAGE_ARMOR;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class MageArmorAssetTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, MAGE_ARMOR, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, MAGE_ARMOR);
	}

	@Override
	protected void beforeSetUp() {
		setUpFireMage(MAGE_ARMOR);
	}
}

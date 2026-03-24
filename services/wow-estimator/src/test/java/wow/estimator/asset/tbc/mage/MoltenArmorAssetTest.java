package wow.estimator.asset.tbc.mage;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.MOLTEN_ARMOR;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class MoltenArmorAssetTest extends TbcAssetTest {
	@Test
	void asset_has_effect() {
		assertHasEffect(player, MOLTEN_ARMOR);
	}

	@Override
	protected void beforeSetUp() {
		setUpFireMage(MOLTEN_ARMOR);
		assetOnly();
	}
}

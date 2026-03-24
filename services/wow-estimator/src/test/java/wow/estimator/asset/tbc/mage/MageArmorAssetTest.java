package wow.estimator.asset.tbc.mage;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.MAGE_ARMOR;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class MageArmorAssetTest extends TbcAssetTest {
	@Test
	void asset_has_effect() {
		assertHasEffect(player, MAGE_ARMOR);
	}

	@Override
	protected void beforeSetUp() {
		setUpFireMage(MAGE_ARMOR);
		assetOnly();
	}
}

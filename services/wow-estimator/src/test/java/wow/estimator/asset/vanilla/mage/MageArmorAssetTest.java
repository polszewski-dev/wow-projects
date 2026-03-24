package wow.estimator.asset.vanilla.mage;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.MAGE_ARMOR;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class MageArmorAssetTest extends VanillaAssetTest {
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

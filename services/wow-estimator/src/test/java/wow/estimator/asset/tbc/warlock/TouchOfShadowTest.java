package wow.estimator.asset.tbc.warlock;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.TOUCH_OF_SHADOW;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class TouchOfShadowTest extends TbcAssetTest {
	@Test
	void asset_has_effect() {
		assertHasEffect(player, TOUCH_OF_SHADOW);
	}

	@Override
	protected void beforeSetUp() {
		setUpDestroWarlock(TOUCH_OF_SHADOW);
		assetOnly();
	}
}

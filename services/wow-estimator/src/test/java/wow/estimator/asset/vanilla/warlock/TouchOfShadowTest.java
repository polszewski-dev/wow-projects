package wow.estimator.asset.vanilla.warlock;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AssetNames.TOUCH_OF_SHADOW;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class TouchOfShadowTest extends VanillaAssetTest {
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

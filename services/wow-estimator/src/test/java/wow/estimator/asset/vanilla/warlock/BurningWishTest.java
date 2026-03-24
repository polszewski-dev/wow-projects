package wow.estimator.asset.vanilla.warlock;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AssetNames.BURNING_WISH;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class BurningWishTest extends VanillaAssetTest {
	@Test
	void asset_has_effect() {
		assertHasEffect(player, BURNING_WISH);
	}

	@Override
	protected void beforeSetUp() {
		setUpDestroWarlock(BURNING_WISH);
		assetOnly();
	}
}

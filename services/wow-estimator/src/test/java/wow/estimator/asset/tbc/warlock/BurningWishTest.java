package wow.estimator.asset.tbc.warlock;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.BURNING_WISH;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class BurningWishTest extends TbcAssetTest {
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

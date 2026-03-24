package wow.estimator.asset.vanilla.priest;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.SHADOWFORM;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class ShadowformAssetTest extends VanillaAssetTest {
	@Test
	void asset_has_effect() {
		assertHasEffect(player, SHADOWFORM);
	}

	@Override
	protected void beforeSetUp() {
		setUpShadowPriest(SHADOWFORM);
		assetOnly();
	}
}

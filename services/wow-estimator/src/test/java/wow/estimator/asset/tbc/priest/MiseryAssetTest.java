package wow.estimator.asset.tbc.priest;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.MISERY;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class MiseryAssetTest extends TbcAssetTest {
	@Test
	void target_has_effect() {
		assertHasEffect(target, MISERY);
	}

	@Override
	protected void beforeSetUp() {
		setUpShadowPriest(MISERY);
	}
}

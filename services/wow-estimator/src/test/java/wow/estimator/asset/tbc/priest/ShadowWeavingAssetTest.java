package wow.estimator.asset.tbc.priest;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.SHADOW_WEAVING;
import static wow.test.commons.EffectNames.SHADOW_VULNERABILITY;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class ShadowWeavingAssetTest extends TbcAssetTest {
	@Test
	void target_has_effect() {
		assertHasEffect(target, SHADOW_VULNERABILITY, 5);
	}

	@Override
	protected void beforeSetUp() {
		setUpShadowPriest(SHADOW_WEAVING);
	}
}

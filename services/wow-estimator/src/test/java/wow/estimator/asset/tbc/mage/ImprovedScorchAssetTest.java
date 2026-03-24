package wow.estimator.asset.tbc.mage;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.IMPROVED_SCORCH;
import static wow.test.commons.EffectNames.FIRE_VULNERABILITY;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class ImprovedScorchAssetTest extends TbcAssetTest {
	@Test
	void target_has_effect() {
		assertHasEffect(target, FIRE_VULNERABILITY, 5);
	}

	@Override
	protected void beforeSetUp() {
		setUpFireMage(IMPROVED_SCORCH);
	}
}

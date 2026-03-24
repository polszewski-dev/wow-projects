package wow.estimator.asset.vanilla.warlock;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.DEMON_SKIN;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class DemonSkinAssetTest extends VanillaAssetTest {
	@Test
	void asset_has_effect() {
		assertHasEffect(player, DEMON_SKIN);
	}

	@Override
	protected void beforeSetUp() {
		level = 19;
		setUpDestroWarlock(DEMON_SKIN);
		assetOnly();
	}
}

package wow.estimator.asset.vanilla.warlock;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.DEMON_ARMOR;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class DemonArmorAssetTest extends VanillaAssetTest {
	@Test
	void asset_has_effect() {
		assertHasEffect(player, DEMON_ARMOR);
	}

	@Override
	protected void beforeSetUp() {
		setUpDestroWarlock(DEMON_ARMOR);
		assetOnly();
	}
}

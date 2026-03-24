package wow.estimator.asset.vanilla.druid;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AssetNames.MOONKIN_AURA;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class MoonkinAuraAssetTest extends VanillaAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, MOONKIN_AURA);
	}

	@Override
	protected void beforeSetUp() {
		setUpBalanceDruid(MOONKIN_AURA);
	}
}

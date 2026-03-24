package wow.estimator.asset.tbc.paladin;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.SANCTITY_AURA;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class SanctityAuraAssetTest extends TbcAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, SANCTITY_AURA);
	}

	@Override
	protected void beforeSetUp() {
		setUpRetributionPaladin(SANCTITY_AURA);
	}
}

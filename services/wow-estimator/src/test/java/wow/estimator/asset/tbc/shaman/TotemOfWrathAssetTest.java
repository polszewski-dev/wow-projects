package wow.estimator.asset.tbc.shaman;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.TOTEM_OF_WRATH;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class TotemOfWrathAssetTest extends TbcAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, TOTEM_OF_WRATH);
	}

	@Override
	protected void beforeSetUp() {
		setUpElementalShaman(TOTEM_OF_WRATH);
	}
}

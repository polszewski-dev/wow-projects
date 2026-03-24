package wow.estimator.asset.tbc.shaman;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.WRATH_OF_AIR_TOTEM;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class WrathOfAirTotemAssetTest extends TbcAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, WRATH_OF_AIR_TOTEM);
	}

	@Override
	protected void beforeSetUp() {
		setUpElementalShaman(WRATH_OF_AIR_TOTEM);
	}
}

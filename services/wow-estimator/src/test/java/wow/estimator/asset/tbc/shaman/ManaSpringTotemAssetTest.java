package wow.estimator.asset.tbc.shaman;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.MANA_SPRING_TOTEM;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class ManaSpringTotemAssetTest extends TbcAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, MANA_SPRING_TOTEM);
	}

	@Override
	protected void beforeSetUp() {
		setUpElementalShaman(MANA_SPRING_TOTEM);
	}
}

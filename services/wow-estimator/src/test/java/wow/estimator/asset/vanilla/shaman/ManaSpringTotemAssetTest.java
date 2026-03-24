package wow.estimator.asset.vanilla.shaman;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AssetNames.MANA_SPRING_TOTEM;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class ManaSpringTotemAssetTest extends VanillaAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, MANA_SPRING_TOTEM);
	}

	@Override
	protected void beforeSetUp() {
		setUpElementalShaman(MANA_SPRING_TOTEM);
	}
}

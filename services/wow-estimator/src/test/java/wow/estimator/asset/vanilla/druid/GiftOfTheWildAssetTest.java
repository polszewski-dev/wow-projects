package wow.estimator.asset.vanilla.druid;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.GIFT_OF_THE_WILD;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class GiftOfTheWildAssetTest extends VanillaAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, GIFT_OF_THE_WILD);
	}

	@Override
	protected void beforeSetUp() {
		setUpBalanceDruid(GIFT_OF_THE_WILD);
	}
}

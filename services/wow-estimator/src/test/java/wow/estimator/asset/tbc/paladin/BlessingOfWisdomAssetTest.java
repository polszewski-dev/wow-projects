package wow.estimator.asset.tbc.paladin;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.BLESSING_OF_WISDOM;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class BlessingOfWisdomAssetTest extends TbcAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, BLESSING_OF_WISDOM);
	}

	@Override
	protected void beforeSetUp() {
		level = 20;
		setUpHolyPaladin(BLESSING_OF_WISDOM);
	}
}

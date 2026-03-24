package wow.estimator.asset.tbc.mage;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.ARCANE_BRILLIANCE;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class ArcaneBrillianceAssetTest extends TbcAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, ARCANE_BRILLIANCE);
	}

	@Override
	protected void beforeSetUp() {
		setUpFireMage(ARCANE_BRILLIANCE);
	}
}

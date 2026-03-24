package wow.estimator.asset.vanilla.mage;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.ARCANE_BRILLIANCE;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class ArcaneBrillianceAssetTest extends VanillaAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, ARCANE_BRILLIANCE);
	}

	@Override
	protected void beforeSetUp() {
		setUpFireMage(ARCANE_BRILLIANCE);
	}
}

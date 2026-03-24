package wow.estimator.asset.vanilla.mage;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.ARCANE_INTELLECT;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class ArcaneIntellectAssetTest extends VanillaAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, ARCANE_INTELLECT);
	}

	@Override
	protected void beforeSetUp() {
		level = 20;
		setUpFireMage(ARCANE_INTELLECT);
	}
}

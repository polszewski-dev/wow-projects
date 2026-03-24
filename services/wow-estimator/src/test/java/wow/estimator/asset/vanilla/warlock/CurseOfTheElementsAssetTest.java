package wow.estimator.asset.vanilla.warlock;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.CURSE_OF_THE_ELEMENTS;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class CurseOfTheElementsAssetTest extends VanillaAssetTest {
	@Test
	void target_has_effect() {
		assertHasEffect(target, CURSE_OF_THE_ELEMENTS);
	}

	@Override
	protected void beforeSetUp() {
		setUpAfflictionWarlock(CURSE_OF_THE_ELEMENTS);
	}
}

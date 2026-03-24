package wow.estimator.asset.tbc.warlock;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.CURSE_OF_THE_ELEMENTS;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class CurseOfTheElementsAssetTest extends TbcAssetTest {//todo brakuje duzo assetow!!!
	@Test
	void target_has_effect() {
		assertHasEffect(target, CURSE_OF_THE_ELEMENTS);
	}

	@Override
	protected void beforeSetUp() {
		setUpAfflictionWarlock(CURSE_OF_THE_ELEMENTS);
	}
}

package wow.estimator.asset.vanilla.priest;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.DIVINE_SPIRIT;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class DivineSpiritAssetTest extends VanillaAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, DIVINE_SPIRIT);
	}

	@Override
	protected void beforeSetUp() {
		level = 30;
		setUpDisciplinePriest(DIVINE_SPIRIT);
	}
}

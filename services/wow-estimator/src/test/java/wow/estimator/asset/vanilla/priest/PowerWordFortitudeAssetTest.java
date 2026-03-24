package wow.estimator.asset.vanilla.priest;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.POWER_WORD_FORTITUDE;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class PowerWordFortitudeAssetTest extends VanillaAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, POWER_WORD_FORTITUDE);
	}

	@Override
	protected void beforeSetUp() {
		level = 20;
		setUpDisciplinePriest(POWER_WORD_FORTITUDE);
	}
}

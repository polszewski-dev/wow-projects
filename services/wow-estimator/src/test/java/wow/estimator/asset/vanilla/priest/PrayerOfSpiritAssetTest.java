package wow.estimator.asset.vanilla.priest;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.PRAYER_OF_SPIRIT;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class PrayerOfSpiritAssetTest extends VanillaAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, PRAYER_OF_SPIRIT);
	}

	@Override
	protected void beforeSetUp() {
		setUpDisciplinePriest(PRAYER_OF_SPIRIT);
	}
}

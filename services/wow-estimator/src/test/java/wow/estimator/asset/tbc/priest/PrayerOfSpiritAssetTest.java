package wow.estimator.asset.tbc.priest;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.PRAYER_OF_SPIRIT;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class PrayerOfSpiritAssetTest extends TbcAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, PRAYER_OF_SPIRIT);
	}

	@Override
	protected void beforeSetUp() {
		setUpDisciplinePriest(PRAYER_OF_SPIRIT);
	}
}

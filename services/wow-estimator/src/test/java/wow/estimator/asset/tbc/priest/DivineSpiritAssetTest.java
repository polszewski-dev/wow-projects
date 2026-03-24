package wow.estimator.asset.tbc.priest;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.DIVINE_SPIRIT;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class DivineSpiritAssetTest extends TbcAssetTest {
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

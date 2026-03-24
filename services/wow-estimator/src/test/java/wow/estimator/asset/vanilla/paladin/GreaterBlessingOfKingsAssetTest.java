package wow.estimator.asset.vanilla.paladin;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.commons.model.character.RaceId.HUMAN;
import static wow.test.commons.AbilityNames.GREATER_BLESSING_OF_KINGS;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class GreaterBlessingOfKingsAssetTest extends VanillaAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, GREATER_BLESSING_OF_KINGS);
	}

	@Override
	protected void beforeSetUp() {
		raceId = HUMAN;
		setUpHolyPaladin(GREATER_BLESSING_OF_KINGS);
	}
}

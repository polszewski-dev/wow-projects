package wow.estimator.asset.vanilla.paladin;

import org.junit.jupiter.api.Test;
import wow.estimator.asset.vanilla.VanillaAssetTest;

import static wow.commons.model.character.RaceId.HUMAN;
import static wow.test.commons.AssetNames.SANCTITY_AURA;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class SanctityAuraAssetTest extends VanillaAssetTest {
	@Test
	void player_has_effect() {
		assertHasEffect(player, SANCTITY_AURA);
	}

	@Override
	protected void beforeSetUp() {
		raceId = HUMAN;
		setUpRetributionPaladin(SANCTITY_AURA);
	}
}

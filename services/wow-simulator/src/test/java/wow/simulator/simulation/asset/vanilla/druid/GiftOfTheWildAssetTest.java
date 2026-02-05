package wow.simulator.simulation.asset.vanilla.druid;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.vanilla.VanillaAssetTest;

import static wow.test.commons.AbilityNames.GIFT_OF_THE_WILD;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class GiftOfTheWildAssetTest extends VanillaAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, GIFT_OF_THE_WILD, player);
	}

	@Test
	void player_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(player, GIFT_OF_THE_WILD);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, GIFT_OF_THE_WILD);
	}

	@Override
	protected void beforeSetUp() {
		setUpBalanceDruid(GIFT_OF_THE_WILD);
	}
}

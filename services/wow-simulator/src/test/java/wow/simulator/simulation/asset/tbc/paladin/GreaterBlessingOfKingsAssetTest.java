package wow.simulator.simulation.asset.tbc.paladin;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.tbc.TbcAssetTest;

import static wow.test.commons.AbilityNames.GREATER_BLESSING_OF_KINGS;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class GreaterBlessingOfKingsAssetTest extends TbcAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, GREATER_BLESSING_OF_KINGS, player);
	}

	@Test
	void player_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(player, GREATER_BLESSING_OF_KINGS);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, GREATER_BLESSING_OF_KINGS);
	}

	@Override
	protected void beforeSetUp() {
		setUpHolyPaladin(GREATER_BLESSING_OF_KINGS);
	}
}

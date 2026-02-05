package wow.simulator.simulation.asset.tbc.shaman;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.tbc.TbcAssetTest;

import static wow.test.commons.AssetNames.WRATH_OF_AIR_TOTEM;

/**
 * User: POlszewski
 * Date: 2026-03-16
 */
class WrathOfAirTotemAssetTest extends TbcAssetTest {
	@Test
	void ability_is_cast() {
		assertSpellCast(0, WRATH_OF_AIR_TOTEM, partyAsset);
	}

	@Test
	void asset_has_buff_for_entire_simulation() {
		assertHasEffectForEntireSimulation(partyAsset, WRATH_OF_AIR_TOTEM);
	}

	@Override
	protected void beforeSetUp() {
		setUpElementalShaman(WRATH_OF_AIR_TOTEM);
	}
}
